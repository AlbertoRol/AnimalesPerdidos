package com.alberto.reportemascotasperdidas.Models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Tamano(var idTamano:Int? = null, var tamano: String? = null) {
}