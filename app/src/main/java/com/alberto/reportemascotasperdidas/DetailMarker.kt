package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.collect.Maps
import com.limerse.slider.ImageCarousel
import com.limerse.slider.listener.CarouselListener
import com.limerse.slider.model.CarouselItem
import java.lang.Exception

class DetailMarker : Fragment() {
    private val TAG = "DetailMarker"

    private val model: VMDatos by activityViewModels()
    private var datosAnimal: TextInputEditText? = null
    private var datosTamano: TextInputEditText? = null
    private var color: TextInputLayout? = null
    private var caracteristicas: TextInputLayout? = null
    private var fecha: TextInputEditText? = null
    private var hora: TextInputEditText? = null

    private var key: String ?= null
    private var animal: String ?= null
    private var tamano: String ?= null
    private var c: String ?= null
    private var caract: String ?= null
    private var f: String ?= null
    private var hm: String ?= null
    private val list = mutableListOf<CarouselItem>()

    private lateinit var idUsuario: String
    private lateinit var nombre: String
    private lateinit var email: String
    private lateinit var foto: String
    private var arrayDatosUsuario: ArrayList<DatosPerfilUsuario> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_marker, container, false)


        model.key.observe(viewLifecycleOwner, { k ->
            Log.d(TAG, "key: $k")
            key = k
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
        }

        toolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId) {
                R.id.compartir -> {
                    saludar("hola")
                    true
                }
                R.id.eliminar -> {
                    model.deleteReportKey(key!!)
                    model.deleteReport.observe(viewLifecycleOwner, {
                        Log.d(TAG, "delete: $it")
                        if (it == 0){
                            mainMaps()
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

        model.reporteById.observe(viewLifecycleOwner, {
            Log.d(ModalBottomSheet.TAG, "id: " + it)
            for (reportes in it){
                animal = reportes.tipo
                tamano = reportes.tamano
                c = reportes.color
                caract = reportes.caracteristicas
                f = reportes.fecha
                hm = reportes.hm
                try {
                    reportes.arrayImagenes!!.forEach {
                        Log.d(ModalBottomSheet.TAG, "onCreateView: " + it)
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

            val carousel: ImageCarousel = view.findViewById(R.id.carousel)
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

    fun mainMaps(){
        val intent = Intent(requireContext(), MapsActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(requireContext(),R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())
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


}