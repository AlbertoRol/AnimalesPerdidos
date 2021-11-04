package com.alberto.reportemascotasperdidas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.Models.Reportes
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.alberto.reportemascotasperdidas.adapters.AdapterMisReportes
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentMisReportes : Fragment() {

    private val TAG = "MisReportes"
    private val model: VMDatos by activityViewModels()
    lateinit var recyclerView: RecyclerView
    private var arrayGetReportes: ArrayList<GetReportes> = ArrayList()
    private var arrayReportes: ArrayList<Reportes> = ArrayList()
    private var arrayKeysUsuarios: ArrayList<String> = ArrayList()
    private var idUser: String? = null
    private lateinit var atras: FloatingActionButton

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
        val view: View = inflater.inflate(R.layout.fragment_mis_reportes, container, false)

        atras = view.findViewById(R.id.atras)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        model.datosUsuario.observe(viewLifecycleOwner, { datos ->
                for (datosUsuario in datos){
                    idUsuario = datosUsuario.idUsuario!!
                    nombre = datosUsuario.nombre!!
                    email = datosUsuario.email!!
                    foto = datosUsuario.img!!
                }
            arrayDatosUsuario.add(DatosPerfilUsuario(idUsuario,nombre,email,foto))
        })

        model.keyUser.observe(viewLifecycleOwner, {
            idUser = it
            model.getMisAltas(it).observe(viewLifecycleOwner, {
                Log.d(TAG, "hola: " + it.size)
                for(i in 0 until it.size){
                    arrayKeysUsuarios.add(it[i].keys!![i])
                    arrayReportes.add(Reportes(it[i].reportes!![i].tipo,it[i].reportes!![i].tamano,it[i].reportes!![i].color,
                        it[i].reportes!![i].caracteristicas,it[i].reportes!![i].fecha,it[i].reportes!![i].hm,it[i].reportes!![i].correo,it[i].reportes!![i].coordenadas,it[i].reportes!![i].arrayImagenes))
                    arrayGetReportes.add(GetReportes(arrayKeysUsuarios,arrayReportes))
                }
                var adapter = AdapterMisReportes(arrayGetReportes)
                recyclerView.adapter = adapter
                adapter.setOnItemClickListener(object: AdapterMisReportes.onItemClickListener{
                    override fun onItemClick(idMarker: String) {
                        var arrayKeys :ArrayList<String> = ArrayList()
                        model.getReporteById(idUser.toString(),idMarker)
                        arrayKeys.add(idUser.toString())
                        arrayKeys.add(idMarker.toString())

                        model.setKey(arrayKeys)
                        model.datosPerfilUsuario(arrayDatosUsuario)
                        val detailMarkers = DetailMarker()
                        detailFraagment(detailMarkers)
                    }
                })
            })
        })

        atras.setOnClickListener {
            val mapsFragment = FragmentMaps()
            detailFraagment(mapsFragment)
            model.cleanMisAltas()
        }

        return view
    }

    fun detailFraagment(fragment: Fragment){
        val nuevoReporte = fragment
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, nuevoReporte)
        // Apply the transaction
        ft?.commit()
    }


}