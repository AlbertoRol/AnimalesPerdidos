package com.alberto.reportemascotasperdidas

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.limerse.slider.ImageCarousel
import com.limerse.slider.model.CarouselItem
import org.json.JSONObject
import kotlin.math.log

class ModalBottomSheet : BottomSheetDialogFragment() {

    private val model: VMDatos by activityViewModels()
    private var datosAnimal: TextInputEditText? = null
    private var datosTamano: TextInputEditText? = null
    private var color: TextInputLayout? = null
    private var caracteristicas: TextInputLayout? = null
    private var fecha: TextInputEditText? = null
    private var hora: TextInputEditText? = null

    var animal: String ?= null
    var tamano: String ?= null
    var c: String ?= null
    var caract: String ?= null
    var f: String ?= null


    companion object {
        const val TAG = "ModalBottomSheet"
    }
    val list = mutableListOf<CarouselItem>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)


        datosAnimal = view.findViewById(R.id.datosAnimal)
        datosTamano = view.findViewById(R.id.datosTamano)
        color = view.findViewById(R.id.tfColor)
        caracteristicas = view.findViewById(R.id.tfCaracteristicas)
        fecha = view.findViewById(R.id.date)
        hora = view.findViewById(R.id.time)

        model.reporteById.observe(viewLifecycleOwner, {
            Log.d(TAG, "id: " + it)
            for (reportes in it){
                animal = reportes.tipo
                tamano = reportes.tamano
                c = reportes.color
                caract = reportes.caracteristicas
                f = reportes.fecha
                reportes.arrayImagenes!!.forEach {
                    Log.d(TAG, "onCreateView: " + it)
                    list.add(
                        CarouselItem(
                            imageUrl = it.toString()
                        )
                    )
                }
            }

            datosAnimal!!.setText(animal)
            datosTamano!!.setText(tamano)
            color!!.editText!!.setText(c)
            caracteristicas!!.editText!!.setText(caract)
            fecha!!.setText(f)

            val carousel: ImageCarousel = view.findViewById(R.id.carousel)
            carousel.setData(list)
        })



        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "hola perros: ")
    }
}