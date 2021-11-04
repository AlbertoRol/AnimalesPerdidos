package com.alberto.reportemascotasperdidas.ViewModel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alberto.reportemascotasperdidas.Interface.*
import com.alberto.reportemascotasperdidas.Models.*
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

    private var _reportById = MutableLiveData<ArrayList<Reportes>>()
    val reporteById: LiveData<ArrayList<Reportes>> get() = _reportById

    private val _urlImage = MutableLiveData<String>()
    val urlImage: LiveData<String> get() = _urlImage

    private val _deleteReport = MutableLiveData<Int>()
    val deleteReport: LiveData<Int> get() = _deleteReport

    private val _key = MutableLiveData<ArrayList<String>>()
    val key: LiveData<ArrayList<String>> get() = _key

    private val _keyUser = MutableLiveData<String>()
    val keyUser: LiveData<String> get() = _keyUser

    private val _datosUsuario = MutableLiveData<ArrayList<DatosPerfilUsuario>>()
    val datosUsuario: LiveData<ArrayList<DatosPerfilUsuario>> get() = _datosUsuario

    private var _altas = MutableLiveData<ArrayList<GetReportes>>()

    private var _misAltas= MutableLiveData<ArrayList<GetReportes>>()

    private val _keysUsers: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>().also {
        }
    }

    fun cleanReportes(){
        _altas =  MutableLiveData<ArrayList<GetReportes>>()
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
    fun insertAlta(reportes: Reportes,idUsuario: String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarAlta(reportes, idUsuario, object : InterfaceAlta {
                override fun onResponceAlta(key: String) {
                    _alta.postValue(key)
                }
            })
        }
    }

    //Insertar foto en alta de reporte
    fun insertFotosAlta(key:String,arrayImagenes: ArrayList<String>, idUsuario:String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarFotosAlta(key, arrayImagenes, idUsuario ,object : InterfaceFotos{
                override fun onResponseAltaFotos(entero: Int) {
                    _imagenes.postValue(entero)
                }
            })
        }
    }

    //Insertar foto en alta de reporte
    fun insertCoordenadas(key:String, latitude: String, longitude:String, idUsuario:String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.insertarCoordenadas(key,latitude,longitude, idUsuario, object: InterfaceCoordenadas{
                override fun onResponseCoordenadas(entero: Int) {
                    _coordenadas.postValue(entero)
                }
            })
        }
    }

    //Insertar coordenadas
    fun altas(keyUser: ArrayList<String>): MutableLiveData<ArrayList<GetReportes>>{
        viewModelScope.launch { Dispatchers.IO
            FirebaseReportesService.getAllReportes(keyUser,object: InterGetAllReportes{
                override fun onResponseGetAllReportes(reportes: ArrayList<GetReportes>) {
                    _altas.postValue(reportes)
                }
            })
        }
        return _altas
    }

    fun getAltas(keysUser: ArrayList<String>): LiveData<ArrayList<GetReportes>> {
        return altas(keysUser)
    }

    fun misAltas(keyUser: String): MutableLiveData<ArrayList<GetReportes>>{
        viewModelScope.launch { Dispatchers.IO
            FirebaseReportesService.getMisReportes(keyUser,object: InterGetMisReportes{
                override fun onResponseGetMisReportes(reportes: ArrayList<GetReportes>) {
                    _misAltas.postValue(reportes)
                }
            })
        }
        return _misAltas
    }

    fun getMisAltas(keysUser: String): LiveData<ArrayList<GetReportes>> {
        return misAltas(keysUser)
    }

    fun cleanMisAltas() {
        _misAltas = MutableLiveData<ArrayList<GetReportes>>()
    }


    fun keysUsers(): MutableLiveData<ArrayList<String>>{
        viewModelScope.launch { Dispatchers.IO
            FirebaseReportesService.getAllKeysUsers(object : InterGetAllKeysUsers{
                override fun onResponseGetAllKeysUsers(keysUsers: ArrayList<String>) {
                    _keysUsers.postValue(keysUsers)
                }
            })
        }
        return _keysUsers
    }

    fun getKeysUsers(): LiveData<ArrayList<String>> {
        return keysUsers()
    }

    fun getReporteById(keyUsers: String,id:String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.getReporteById(keyUsers,id, object: InterGetIdReportes{
                override fun onResponseGetIdReportes(reportes: ArrayList<Reportes>) {
                    _reportById.postValue(reportes)
                }
            })
        }
    }

    fun cleanReportById(){
        _reportById = MutableLiveData<ArrayList<Reportes>>()
    }

    fun urlImage(url: String){
        viewModelScope.launch(Dispatchers.IO){
            _urlImage.postValue(url)
        }
    }

    fun setKey(arrayID:ArrayList<String>){
        viewModelScope.launch(Dispatchers.IO){
            _key.postValue(arrayID)
        }
    }

    fun deleteReportKey(keyUsuario:String,key: String){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseReportesService.deleteReport(keyUsuario,key, object: InterfaceDeleteReport{
                override fun onResponseDeleteReport(entero: Int) {
                    _deleteReport.postValue(entero)
                }
            })
        }
    }

    fun setDatosUsuario(arrayDatosPerfilUsuario: ArrayList<DatosPerfilUsuario>){
        Log.d(TAG, "array: " + arrayDatosPerfilUsuario.size)
        viewModelScope.launch(Dispatchers.IO){
            _datosUsuario.postValue(arrayDatosPerfilUsuario)
        }
    }

    fun setKeyUserMisReportes(keyUser: String){
        viewModelScope.launch(Dispatchers.IO){
            _keyUser.postValue(keyUser)
        }
    }

    fun datosPerfilUsuario(arrayUsuario: ArrayList<DatosPerfilUsuario>){
        Log.d(TAG, "size: " + arrayUsuario.size)
        viewModelScope.launch(Dispatchers.IO){
            _datosUsuario.postValue(arrayUsuario)
        }
    }

}