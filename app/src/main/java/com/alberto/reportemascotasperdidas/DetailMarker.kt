package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.CallbackManager
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.collect.Maps
import com.limerse.slider.ImageCarousel
import com.limerse.slider.listener.CarouselListener
import com.limerse.slider.model.CarouselItem
import java.lang.Exception
import com.facebook.share.widget.ShareDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DetailMarker : Fragment() {
    private val TAG = "DetailMarker"

    private val model: VMDatos by activityViewModels()
    private var datosAnimal: TextInputEditText? = null
    private var datosTamano: TextInputEditText? = null
    private var color: TextInputLayout? = null
    private var caracteristicas: TextInputLayout? = null
    private var fecha: TextInputEditText? = null
    private var hora: TextInputEditText? = null
    private var correo: TextInputEditText? = null

    private var key: String ?= null
    private var animal: String ?= null
    private var tamano: String ?= null
    private var c: String ?= null
    private var caract: String ?= null
    private var f: String ?= null
    private var hm: String ?= null
    private var email: String ?= null
    private var latitude: String ?= null
    private var longitude: String ?= null


    private var keyUsuario:String?= null
    private var keyMarker:String?= null

    private lateinit var callBackManager: CallbackManager
    private lateinit var shareDialog: ShareDialog

    private var arrayNumFotos: ArrayList<String> = ArrayList()


    private val list = mutableListOf<CarouselItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_marker, container, false)

        callBackManager = CallbackManager.Factory.create()
        shareDialog =  ShareDialog(this);

        val carousel: ImageCarousel = view.findViewById(R.id.carousel)
        model.key.observe(viewLifecycleOwner, { keys ->
            Log.d(TAG, "key: ${keys.size}")
            keyUsuario = keys.get(0)
            keyMarker = keys.get(1)
        })

        val collapsingToolbarLayout = view.findViewById<CollapsingToolbarLayout>(R.id.collapsingTool)
        collapsingToolbarLayout.title = " "
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.tool)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val fragmentMaps = FragmentMaps()
            openFrgament(fragmentMaps)
            model.cleanReportById()
        }

        toolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId) {
                R.id.compartir -> {
                    compartir()
                    true
                }
                R.id.eliminar -> {
                    model.deleteReportKey(keyUsuario!!,keyMarker!!)
                    model.deleteReport.observe(viewLifecycleOwner, {
                        borrarFotos()
                        Log.d(TAG, "delete: $it")
                        if (it == 0){
                            val fragmentMaps = FragmentMaps()
                            openFrgament(fragmentMaps)
                            model.cleanReportes()
                            model.cleanReportById()
                        }
                    })
                    true
                }
                else -> false
            }
        }


        datosAnimal = view.findViewById(R.id.datosAnimal)
        datosTamano = view.findViewById(R.id.datosTamano)
        color = view.findViewById(R.id.tfColor)
        caracteristicas = view.findViewById(R.id.tfCaracteristicas)
        fecha = view.findViewById(R.id.date)
        hora = view.findViewById(R.id.time)
        correo = view.findViewById(R.id.correo)

        model.reporteById.observe(viewLifecycleOwner, {
            var numero: Int = 0
            Log.d(TAG, "id: " + it)
            for (reportes in it){
                animal = reportes.tipo
                tamano = reportes.tamano
                c = reportes.color
                caract = reportes.caracteristicas
                f = reportes.fecha
                hm = reportes.hm
                email = reportes.correo
                latitude = reportes.coordenadas!!.get(0)
                longitude = reportes.coordenadas.get(1)
                try {
                    reportes.arrayImagenes!!.forEach {
                        Log.d(TAG, "onCreateView: " + it)
                        numero++
                        arrayNumFotos.add(numero.toString())
                        list.add(
                            CarouselItem(
                                imageUrl = it.toString()
                            )
                        )
                    }
                }catch (ex: Exception){

                }
            }

            datosAnimal!!.setText(animal)
            datosTamano!!.setText(tamano)
            color!!.editText!!.setText(c)
            caracteristicas!!.editText!!.setText(caract)
            fecha!!.setText(f)
            hora!!.setText(hm)
            correo!!.setText(email)

            carousel.setData(list)
            carousel.carouselListener = object : CarouselListener {
                override fun onClick(position: Int, carouselItem: CarouselItem) {
                    Log.d(TAG, "item: ${carouselItem.imageUrl}")
                    model.urlImage(carouselItem.imageUrl!!)
                    val zoomFragment = ZoomFragment()
                    openFrgament(zoomFragment)
                }
            }
        })

        return view
    }

    fun openFrgament(fragment: Fragment){
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, fragment)
        // Apply the transaction
        ft?.commit()
    }
    
    fun saludar(saludar: String){
        Log.d(TAG, "saludar: $saludar")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun compartir(){
        var shareLinkContent = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse("https://www.google.com/maps/place/$latitude,$longitude"))
            .setShareHashtag(ShareHashtag.Builder()
            .setHashtag("#AniamlesPerdidos")
            .build())
            .build()
        shareDialog.show(shareLinkContent)
    }

    fun borrarFotos(){
        var storage = Firebase.storage
        var storageRef = storage.reference
        for (numero in arrayNumFotos){
            Log.d(TAG, "foto: " + numero)
            val borrar = storageRef.child("altas/$keyUsuario/"+keyMarker+"/"+numero+".jpg")
            borrar.delete().addOnSuccessListener {
                Log.d(TAG, "borrado: ")
            }.addOnFailureListener{
                Log.d(TAG, "no borrado: ")
            }
        }
    }
}