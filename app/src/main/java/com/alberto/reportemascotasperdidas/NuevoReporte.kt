package com.alberto.reportemascotasperdidas


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alberto.reportemascotasperdidas.Models.FormatoFecha
import com.alberto.reportemascotasperdidas.Models.Reportes
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

class NuevoReporte : Fragment() {

    private val TAG = "NuevoRegistro"

    private var guarda: FloatingActionButton? = null
    private var fotosGaleria: FloatingActionButton? = null
    private var atras: FloatingActionButton? = null
    private var spinnerAnimal: TextInputLayout? = null
    private var datosAnimal: AutoCompleteTextView? = null
    private var spinnerTamano: TextInputLayout? = null
    private var datosTamano: AutoCompleteTextView? = null
    private var color: TextInputLayout? = null
    private var caracteristicas: TextInputLayout? = null
    private var fecha: TextInputEditText? = null
    private var hora: TextInputEditText? = null
    private var arrayImagenes: ArrayList<String> = ArrayList()
    private var arrayLocation: ArrayList<String> = ArrayList()

    private var animal: String? = null
    private var tamano: String? = null
    private var latitude: String? = null
    private var longitud: String? = null
    private var hm: String? = null

    private var id: String? = null
    private var nombre: String? = null
    private var email: String? = null
    private var foto: String? = null

    private val model: VMDatos by activityViewModels()
    private var fechaFormato: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                Log.d(TAG, ": " + fileUri.path)
                arrayImagenes.add(fileUri.path.toString())

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //Log.d(TAG, "permitido: ")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Log.d(TAG, "ubicacion " + location!!.latitude + " " + location.longitude)
                        latitude = location!!.latitude.toString()
                        longitud = location!!.longitude.toString()
                    }
            } else {
                Log.d(TAG, "no permitido: ")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_nuevo_reporte, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        model.datosUsuario.observe(viewLifecycleOwner, { datos ->
            Log.d(TAG, "arrayNuevoi: " + datos.size)

            for (datosUsuario  in datos){
                id = datosUsuario.idUsuario
                nombre = datosUsuario.nombre
                email = datosUsuario.email
                foto = datosUsuario.img
            }
        })

        //registerPermissionRequest()
        fotosGaleria = view.findViewById(R.id.fotos)
        spinnerAnimal = view.findViewById(R.id.spinnerAnimal)
        datosAnimal = view.findViewById(R.id.datosAnimal)
        spinnerTamano = view.findViewById(R.id.spinnerTamano)
        datosTamano = view.findViewById(R.id.datosTamano)
        color = view.findViewById(R.id.tfColor)
        caracteristicas = view.findViewById(R.id.tfCaracteristicas)
        fecha = view.findViewById(R.id.date)
        hora = view.findViewById(R.id.time)
        guarda = view.findViewById(R.id.guardar)
        atras = view.findViewById(R.id.atras)

        model.getTipoAnimal()
        model.getTamano()
        permisoUbicacion()

        model.tipoAnimal.observe(viewLifecycleOwner, { it ->
            var tipoAnimal: ArrayList<String> = ArrayList()
            //Log.d(TAG, "datillos: ${it}")
            for (tipo in it) {
                tipoAnimal.add(tipo.toString())
            }
            loadSpinnerTipoAnimal(tipoAnimal)
        })

        model.tamano.observe(viewLifecycleOwner, { it ->
            var tamano: ArrayList<String> = ArrayList()
            //Log.d(TAG, "datillos: ${it}")
            for (tipo in it) {
                tamano.add(tipo.toString())
            }
            loadSpinnerTamano(tamano)
        })

        fecha!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> mostrarCalendario()
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        hora!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> mostrarHora()
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        fotosGaleria!!.setOnClickListener {
            fotos()
        }

        guarda!!.setOnClickListener {
            guardarReporte()
        }

        atras!!.setOnClickListener {
            val fragmentMaps = FragmentMaps()
            detailFraagment(fragmentMaps)
        }

        return view
    }


    fun loadSpinnerTipoAnimal(arrayList: ArrayList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, arrayList)
        (spinnerAnimal!!.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        datosAnimal!!.setOnItemClickListener { adapterView, view, i, l ->
            //Log.d(TAG, "animal: " + adapterView.getItemAtPosition((i)).toString())
            animal = adapterView.getItemAtPosition((i)).toString()
        }
    }

    fun loadSpinnerTamano(arrayList: ArrayList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, arrayList)
        (spinnerTamano!!.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        datosTamano!!.setOnItemClickListener { adapterView, view, i, l ->
            //Log.d(TAG, "tamano: " + adapterView.getItemAtPosition((i)).toString())
            tamano = adapterView.getItemAtPosition((i)).toString()
        }
    }

    fun mostrarCalendario() {

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calendar.timeInMillis


        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(decThisYear)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(parentFragmentManager, "")

        datePicker.addOnPositiveButtonClickListener {
            val formatoFecha = FormatoFecha()
            fechaFormato = formatoFecha.formatDate(it)
            fecha!!.setText(fechaFormato.toString())
            fecha!!.clearFocus()
        }
    }

    fun mostrarHora() {

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(12)
                .setMinute(0)
                .build()

        picker.show(parentFragmentManager, "tag");

        picker.addOnPositiveButtonClickListener {
            var h = picker.hour
            val minuto = picker.minute
            hora!!.setText(h.toString() + ":" + minuto)
            hora!!.clearFocus()
            hm = h.toString() + ":" + minuto
        }
    }

    fun guardarReporte(){
        model.insertAlta(
            Reportes(
                animal, tamano, color!!.editText?.text.toString(),
                caracteristicas!!.editText?.text.toString(), fechaFormato,hm), id!!
        )

        model.alta.observe(viewLifecycleOwner, {
            var key = it
            Log.d(TAG, "alta1: " + it.toString())
            model.insertCoordenadas(it, latitude!!, longitud!!,id!!)
            model.coordenadas.observe(viewLifecycleOwner, {
                //Log.d(TAG, "coordenadas: " + it)
            })
            //arrayLocation.clear()
            if (arrayImagenes.size != 0) {
                model.insertFotosAlta(key, arrayImagenes,id!!)
                model.imagenes.observe(viewLifecycleOwner, {
                    Log.d(TAG, "imagenes: " + it)
                    arrayImagenes.clear()
                })
            }else {
                key = ""
                //Log.d(TAG, "key: " + key)
            }
            mainFragment()
        })
    }

    fun fotos(){
        /*val reportes = Reportes("perro","mediano","cafe","chumuelo lastimado","12/08/67","dsdsadsadsd")
            database.child("reportes").child("altas").push().setValue(reportes)*/
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    fun permisoUbicacion() {
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun mainFragment(){
        val intent = Intent(activity, MapsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("nombre", nombre)
        intent.putExtra("email", email)
        intent.putExtra("foto", foto)
        startActivity(intent)
    }

    fun detailFraagment(fragment: Fragment){
        val mapsFrgament = fragment
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, mapsFrgament)
        // Apply the transaction
        ft?.commit()
    }
}