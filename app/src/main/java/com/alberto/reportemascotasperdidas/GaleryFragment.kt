package com.alberto.reportemascotasperdidas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import java.io.File
import java.io.FileInputStream


class GaleryFragment : Fragment() {


    final val TAG = "Archivos"
    val listImg = arrayListOf<String>()
    var tipoArchivo: Int? = null
    val pickImage = 100
    val picktxt = 200

    var storage = Firebase.storage

    // Create a storage reference from our app
    var storageRef = storage.reference

    // Create a reference to "mountains.jpg"
    val mountainsRef = storageRef.child("mountains.jpg")

    // Create a reference to 'images/mountains.jpg'
    val mountainImagesRef = storageRef.child("images/mountains.jpg")

    private val model: VMDatos by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*model.datosF.observe(viewLifecycleOwner, { item ->
            for (valores in item){
            }
        })*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_galery, container, false)

        archivos()

        return view
    }

    fun archivos(){
        val intent = Intent(requireContext(), FilePickerActivity::class.java)

        intent.putExtra(
            FilePickerActivity.CONFIGS, Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowFiles(true)
                .enableImageCapture(false)
                .enableVideoCapture(false)
                .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                    "zip", "tar", "gz", "rar", "7z","torrent",
                    "doc", "docx", "odt", "ott",
                    "ppt", "pptx", "pps",
                    "xls", "xlsx", "ods", "ots")
                .setMaxSelection(10)
                .setSkipZeroSizeFiles(true)
                .build()
        )

        startActivityForResult(intent, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            val files: ArrayList<MediaFile> = data!!.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!

            val count = files.size
            for (i in 0 until count){
                var nombre = files.get(i).path
                Log.d(TAG, "nombres: " + nombre)
                listImg.add(nombre)

                val stream = FileInputStream(File(nombre))

                var uploadTask = mountainImagesRef.putStream(stream)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "subio: ")
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    Log.d(TAG, "no subio: ")

                }

            }
            formulario()
        }
        formulario()
    }

    fun formulario(){

        val camera = NuevoReporte()
        // Add ObjectDetailFragment by replacing the HomeFragment
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.fade_in,  // popEnter
                R.anim.slide_out, // popExit
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
            )
            ?.replace(R.id.formulario, camera)
        ft?.commit()
    }



}