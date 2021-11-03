package com.alberto.reportemascotasperdidas.Service

import android.net.Uri
import android.util.Log
import com.alberto.reportemascotasperdidas.Interface.*
import com.alberto.reportemascotasperdidas.Models.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

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

    fun insertarAlta(reportes: Reportes,idUusario:String, interfaceAlta: InterfaceAlta){
        try {
            var key = database.push().key
            //Log.d(TAG, "key: " + key)
            database.child("reportes").child("altas").child("usuarios").child(idUusario).child(key.toString()).setValue(reportes)
            interfaceAlta.onResponceAlta(key!!)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Alta: " + exception)
            interfaceAlta.onResponceAlta("")
        }
    }

    fun insertarFotosAlta(key:String,arrayImagenes: ArrayList<String>, idUsuarios:String, interfaceFotos: InterfaceFotos){
        try {
            var arrayPrueba: ArrayList<String> = ArrayList()
            var storage = Firebase.storage
            var storageRef = storage.reference
            var idUsuario = idUsuarios
            var keys = key

            var c = 1
            for (fotos in arrayImagenes){
                val stream = FileInputStream(File(fotos))

                Log.d(TAG, "contyador: " + c)
                val direccion = storageRef.child("altas/$idUsuario/"+key+"/"+c+".jpg")
                var uploadTask = direccion.putStream(stream)

                try {
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "no subio: ")
                    }.addOnSuccessListener { taskSnapshot ->
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                            Log.d(TAG, "url: " + it.result)
                            arrayPrueba.add(it.result.toString())
                            Log.d(TAG, "size: " + arrayPrueba.size)
                            database.child("reportes").child("altas").child("usuarios").child(idUsuario).child(keys).child("arrayImagenes").setValue(arrayPrueba)
                        }
                    }
                }catch (exception: FirebaseException){
                    Log.d(TAG, "error: " + exception)
                }
                c++
            }
            interfaceFotos.onResponseAltaFotos(1)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Imagenes: " + exception)
        }
    }

    fun insertarCoordenadas(key:String, latitude: String, longitude:String, idUsuario:String ,interfaceCoordenadas: InterfaceCoordenadas){
        try {
            var arrayUbicacion : ArrayList<String> = ArrayList()
            arrayUbicacion.add(latitude)
            arrayUbicacion.add(longitude)
            database.child("reportes").child("altas").child("usuarios").child(idUsuario).child(key.toString()).child("coordenadas").setValue(arrayUbicacion)
            interfaceCoordenadas.onResponseCoordenadas(1)
        }catch (exception: FirebaseException){
            Log.d(TAG, "Error Imagenes: " + exception)
        }
    }

    fun getAllKeysUsers(interfaceGetAllKeysUsers: InterGetAllKeysUsers){
        try {
            var arrayKeys: ArrayList<String> = ArrayList()
            database.child("reportes").child("altas").child("usuarios").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (datos in snapshot.children){
                            arrayKeys.add(datos.key!!)
                        }
                        //Log.d(TAG, "size: " + r.arrayImagenes)
                        interfaceGetAllKeysUsers.onResponseGetAllKeysUsers(arrayKeys)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }catch (error:FirebaseException){

        }
    }

    fun getAllReportes(keyUser: ArrayList<String>,interfaceGetAllReportes: InterGetAllReportes){
        try {
            var r = Reportes()
            var allReportes: ArrayList<GetReportes> = ArrayList()
            var reportes: ArrayList<Reportes> = ArrayList()
            var arrayKeys: ArrayList<String> = ArrayList()

            for (i in keyUser){
                database.child("reportes").child("altas").child("usuarios").child(i).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            for (datos in snapshot.children){
                                r = datos.getValue(Reportes::class.java)!!
                                arrayKeys.add(datos.key!!)
                                reportes.add(r)
                                allReportes.add(GetReportes(arrayKeys,reportes))
                            }
                            //Log.d(TAG, "size: " + r.arrayImagenes)
                            interfaceGetAllReportes.onResponseGetAllReportes(allReportes)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }catch (error:FirebaseException){

        }
    }

    fun getReporteById(keyUsers: String,id: String,interfaceGetAllReportes: InterGetIdReportes){
        try {
            var repor = Reportes()
            var reportes: ArrayList<Reportes> = ArrayList()
            var coordenadas: ArrayList<String> = ArrayList()
            var imagenes: ArrayList<String> = ArrayList()
            var keys: ArrayList<String> = ArrayList()
            var getReportes: ArrayList<GetReportes> = ArrayList()
            database.child("reportes").child("altas").child("usuarios").child(keyUsers).child(id).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        repor = snapshot.getValue(Reportes::class.java)!!
                        reportes.add(repor)
                        //Log.d(TAG, "onDataChange: " + repor)
                    }
                    interfaceGetAllReportes.onResponseGetIdReportes(reportes)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }catch (error:FirebaseException){

        }
    }

    fun deleteReport(key: String, interfaceDeleteReport: InterfaceDeleteReport){
        try {
            database.child("reportes/altas").child(key).removeValue()
            interfaceDeleteReport.onResponseDeleteReport(0)
        }catch (error:FirebaseException){
            Log.d(TAG, "deleteReport: $error")
            interfaceDeleteReport.onResponseDeleteReport(1)

        }
    }
    fun getMisReportes(keyUser: String,interfaGetMisReportes: InterGetMisReportes){
        try {
            var r = Reportes()
            var allReportes: ArrayList<GetReportes> = ArrayList()
            var reportes: ArrayList<Reportes> = ArrayList()
            var arrayKeys: ArrayList<String> = ArrayList()

            database.child("reportes").child("altas").child("usuarios").child(keyUser).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (datos in snapshot.children){
                            r = datos.getValue(Reportes::class.java)!!
                            arrayKeys.add(datos.key!!)
                            Log.d(TAG, "keys: " + datos.key!!)
                            reportes.add(r)
                            allReportes.add(GetReportes(arrayKeys,reportes))
                        }
                        //Log.d(TAG, "size: " + r.arrayImagenes)
                        interfaGetMisReportes.onResponseGetMisReportes(allReportes)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        }catch (error:FirebaseException){

        }
    }

}