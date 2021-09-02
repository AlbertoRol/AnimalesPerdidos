package com.alberto.reportemascotasperdidas.Models

import android.util.Log
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class Reportes (val tipo:String? = null, val tamano:String? = null,
                    val color:String? = null, val caracteristicas:String? = null,
                    val fecha:String? = null, val coordenadas: List<String>? = null, val arrayImagenes: List<String>? = null ) {

}