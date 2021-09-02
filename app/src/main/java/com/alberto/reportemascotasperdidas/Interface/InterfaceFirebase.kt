package com.alberto.reportemascotasperdidas.Interface

import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.Models.TipoAnimal

interface InterfaceFirebase {
    fun onResponseSpinner(list: List<String>)
}

interface InterfaceAlta{
    fun onResponceAlta(key: String)
}

interface InterfaceFotos{
    fun onResponseAltaFotos(entero: Int)
}

interface InterGetAllReportes{
    fun onResponseGetAllReportes(reportes: ArrayList<GetReportes>)
}

interface InterfaceCoordenadas{
    fun onResponseCoordenadas(entero: Int)
}