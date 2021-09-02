package com.alberto.reportemascotasperdidas.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alberto.reportemascotasperdidas.Interface.*
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.Models.Reportes
import com.alberto.reportemascotasperdidas.Models.Tamano
import com.alberto.reportemascotasperdidas.Models.TipoAnimal
import com.alberto.reportemascotasperdidas.Service.FirebaseReportesService
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream


class VMDatos: ViewModel() {
    private val TAG : String = "VModel"

    private val _tipoAnimal = MutableLiveData<List<String>>()
    val tipoAnimal: LiveData<List<String>> get() = _tipoAnimal

    private val _tamano = MutableLiveData<List<String>>()
    val tamano: LiveData<List<String>> get() = _tamano

    private val _alta = MutableLiveData<String>()
    val alta: LiveData<String> get() = _alta

    private val _imagenes = MutableLiveData<Int>()
    val imagenes: LiveData<Int> get() = _imagenes

    private val _coordenadas = MutableLiveData<Int>()
    val coordenadas: LiveData<Int> get() = _coordenadas

    private val _altas: MutableLiveData<ArrayList<GetReportes>> by lazy {
        MutableLiveData<ArrayList<GetReportes>>().also {
        }
    }


    fun getTipoAnimal() {
        viewModelScope.launch (Dispatchers.IO){
            FirebaseReportesService.getTipoAnimal(object : InterfaceFirebase{
                override fun onResponseSpinner(animal: List<String>) {
                    _tipoAnimal.postValue(animal)
                }
            })
        }
    }

    fun getTamano() {
        viewModelScope.launch (Dispatchers.IO){
            FirebaseReportesService.getTamano(object  : InterfaceFirebase{
                override fun onResponseSpinner(list: List<String>) {
                    _tamano.postValue(list)
                }
            })
        }
    }

    //Alta reporte
    fun insertAlta(reportes: Reportes){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarAlta(reportes, object : InterfaceAlta {
                override fun onResponceAlta(key: String) {
                    _alta.postValue(key)
                }
            })
        }
    }

    //Insertar foto en alta de reporte
    fun insertFotosAlta(key:String,arrayImagenes: ArrayList<String>){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarFotosAlta(key, arrayImagenes, object : InterfaceFotos{
                override fun onResponseAltaFotos(entero: Int) {
                    _imagenes.postValue(entero)
                }
            })
        }
    }

    //Insertar foto en alta de reporte
    fun insertCoordenadas(key:String, latitude: String, longitude:String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarCoordenadas(key,latitude,longitude, object: InterfaceCoordenadas{
                override fun onResponseCoordenadas(entero: Int) {
                    _coordenadas.postValue(entero)
                }
            })
        }
    }

    //Insertar coordenadas
    fun altas(): MutableLiveData<ArrayList<GetReportes>>{
        viewModelScope.launch { Dispatchers.IO
            FirebaseReportesService.getAllReportes(object: InterGetAllReportes{
                override fun onResponseGetAllReportes(reportes: ArrayList<GetReportes>) {
                    _altas.postValue(reportes)
                }
            })
        }
        return _altas
    }

    fun getAltas(): LiveData<ArrayList<GetReportes>> {
        return altas()
    }

}