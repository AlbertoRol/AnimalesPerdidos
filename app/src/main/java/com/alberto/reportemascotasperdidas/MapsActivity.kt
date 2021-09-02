package com.alberto.reportemascotasperdidas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.alberto.reportemascotasperdidas.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.log


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val TAG = "Main"
    private lateinit var nuevoReporte: FloatingActionButton

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val casa = LatLng(19.37630454300055, -98.99105401230962)
    private lateinit var markerCasa: Marker

    private val model: VMDatos by viewModels()
    val mHashMap = HashMap<Marker, String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitud: String? = null
    private lateinit var camera: LatLng


    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //Log.d(TAG, "permitido: ")
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        Log.d(TAG, "ubicacion " + location!!.latitude + " " + location.longitude)
                        latitude = location!!.latitude.toString()
                        longitud = location!!.longitude.toString()
                        camera = LatLng(latitude!!.toDouble(), longitud!!.toDouble())
                        val zoomLevel = 18.0f
                        mMap.isMyLocationEnabled = true
                        mMap.uiSettings.isZoomControlsEnabled = true
                        mMap.uiSettings.isMyLocationButtonEnabled = true
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, zoomLevel))
                    }
            } else {
                Log.d(TAG, "no permitido: ")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        permisoUbicacion()


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nuevoReporte = findViewById(R.id.nuevo)
        nuevoReporte.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ActivityR::class.java)
            startActivity(intent)
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        model.getAltas().observe(this, Observer<ArrayList<GetReportes>> {
            //Log.d(TAG, "size: " + it.size)
            for(i in 0 until it.size){
                //Log.d(TAG, "keys: " + it[i].keys!![i])
                //Log.d(TAG, "latitud: " + (it[i].reportes!![i].coordenadas!!.get(0)))
                //Log.d(TAG, "longitud: " + (it[i].reportes!![i].coordenadas?.get(1)))
                var marker = LatLng(it[i].reportes!![i].coordenadas!!.get(0).toDouble(), it[i].reportes!![i].coordenadas?.get(1)!!.toDouble())

                markerCasa = mMap.addMarker(MarkerOptions().position(marker).title("Perth"))
                mMap.isMyLocationEnabled()
                mMap.setOnMarkerClickListener(this)
                mHashMap.put(markerCasa,it[i].keys!![i])
            }
        })
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int
        val id = mHashMap.get(marker)

        Log.d(TAG, "id: " + id)

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                this,
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    fun permisoUbicacion() {
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}