package com.alberto.reportemascotasperdidas.Service

import android.util.Log
import com.alberto.reportemascotasperdidas.Interface.*
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.Models.Reportes
import com.alberto.reportemascotasperdidas.Models.Tamano
import com.alberto.reportemascotasperdidas.Models.TipoAnimal
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream

object FirebaseReportesService {
    val TAG = "ReportesService"
    private val database = Firebase.database.reference
    private val databaseAnimal = Firebase.database.reference.child("reportes/TipoAnimal")
    private val databaseTamano = Firebase.database.reference.child("reportes/tamano")


    fun getTipoAnimal(interfaceFirebase: InterfaceFirebase){
        var animales : ArrayList<String> = ArrayList()
        try{
            databaseAnimal.get().addOnCompleteListener(OnCompleteListener<DataSnapshot>() { task ->
                if (task.isSuccessful()){
                    task.result.children.forEach {
                        Log.d(TAG, "getTipoAnimal: " + it.value)
                        animales.add(it.value.toString())
                    }
                    interfaceFirebase.onResponseSpinner(animales)
                }
            })

        }catch (exception: FirebaseException){
            Log.d(TAG, "Error: $exception")
        }
    }

    fun getTamano(interfaceFirebase: InterfaceFirebase){
        var tamano: ArrayList<String> = ArrayList()
        try{
            databaseTamano.get().addOnCompleteListener(OnCompleteListener<DataSnapshot>(){ task ->
                if (task.isSuccessful()){
                    task.result.children.forEach{
                        Log.d(TAG, "getTamano: " + it.value)
                        tamano.add(it.value.toString())
                    }
                    interfaceFirebase.onResponseSpinner(tamano)
                }
            })
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error: $exception")
        }
    }

    fun insertarAlta(reportes: Reportes, interfaceAlta: InterfaceAlta){
        try {
            var key = database.push().key
            //Log.d(TAG, "key: " + key)
            database.child("reportes").child("altas").child(key.toString()).setValue(reportes)
            interfaceAlta.onResponceAlta(key!!)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Alta: " + exception)
            interfaceAlta.onResponceAlta("")
        }
    }

    fun insertarFotosAlta(key:String,arrayImagenes: ArrayList<String>, interfaceFotos: InterfaceFotos){
        try {
            var arrayPrueba: ArrayList<String> = ArrayList()
            var storage = Firebase.storage
            var storageRef = storage.reference

            var c = 1
            for (fotos in arrayImagenes){
                val stream = FileInputStream(File(fotos))

                Log.d(TAG, "contyador: " + c)
                val direccion = storageRef.child("altas/"+key.toString()+"/"+c+".jpg")
                var uploadTask = direccion.putStream(stream)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "no subio: ")
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                        Log.d(TAG, "url: " + it.result)
                        arrayPrueba.add(it.result.toString())
                        Log.d(TAG, "size: " + arrayPrueba.size)
                        database.child("reportes").child("altas").child(key.toString()).child("arrayImagenes").setValue(arrayPrueba)
                    }
                }
                c++
            }
            interfaceFotos.onResponseAltaFotos(1)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Imagenes: " + exception)
        }
    }

    fun insertarCoordenadas(key:String, latitude: String, longitude:String, interfaceCoordenadas: InterfaceCoordenadas){
        try {
            var arrayUbicacion : ArrayList<String> = ArrayList()
            arrayUbicacion.add(latitude)
            arrayUbicacion.add(longitude)
            database.child("reportes").child("altas").child(key.toString()).child("coordenadas").setValue(arrayUbicacion)
            interfaceCoordenadas.onResponseCoordenadas(1)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Imagenes: " + exception)
        }
    }

    fun getAllReportes(interfaceGetAllReportes: InterGetAllReportes){
        try {
            var r = Reportes()
            var allReportes: ArrayList<GetReportes> = ArrayList()
            var reportes: ArrayList<Reportes> = ArrayList()
            var arrayKeys: ArrayList<String> = ArrayList()
            database.child("reportes").child("altas").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (datos in snapshot.children){
                            r = datos.getValue(Reportes::class.java)!!
                            arrayKeys.add(datos.key!!)
                            reportes.add(r)
                            allReportes.add(GetReportes(arrayKeys,reportes))
                        }
                        Log.d(TAG, "size: " + allReportes.size)
                        interfaceGetAllReportes.onResponseGetAllReportes(allReportes)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }catch (error:FirebaseException){

        }
    }
}