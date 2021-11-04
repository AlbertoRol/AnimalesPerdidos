package com.alberto.reportemascotasperdidas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alberto.reportemascotasperdidas.DetailMarker
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.NuevoReporte
import com.alberto.reportemascotasperdidas.R
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.login.LoginManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView





class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var mAdView : AdView
    private val TAG = "Main"
    private lateinit var nuevoReporte: FloatingActionButton

    private lateinit var mMap: GoogleMap
    private lateinit var markerCasa: Marker


    private val model: VMDatos by viewModels()
    val mHashMap = HashMap<Marker, String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitud: String? = null
    private lateinit var camera: LatLng
    private lateinit var materialToolBar: Toolbar;
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var id: String? = null
    private var nombre: String? = null
    private var email: String? = null
    private var foto: String? = null
    private var arrayDatosUsuario: ArrayList<DatosPerfilUsuario> = ArrayList()
    private var arrayKeysUsuarios: ArrayList<String> = ArrayList()
    private var arrayKeysUsuariosReportes: ArrayList<String> = ArrayList()

    private lateinit var idUsuario: String

    private lateinit var appbarLayout: AppBarLayout

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //Log.d(TAG, "permitido: ")
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        //Log.d(TAG, "ubicacion " + location!!.latitude + " " + location.longitude)
                        latitude = location!!.latitude.toString()
                        longitud = location!!.longitude.toString()
                        camera = LatLng(latitude!!.toDouble(), longitud!!.toDouble())
                        val zoomLevel = 18.0f
                        mMap.isMyLocationEnabled = true
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
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        materialToolBar = findViewById(R.id.topAppBar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        appbarLayout = findViewById(R.id.appbar)

        permisoUbicacion()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        model.datosUsuario.observe(this, { datos ->
            Log.d(TAG, "hola: ")

            for (datosUsuario in datos){
                idUsuario = datosUsuario.idUsuario!!
                nombre = datosUsuario.nombre!!
                email = datosUsuario.email!!
                foto = datosUsuario.img!!

                val header = navigationView.getHeaderView(0)
                val panel = header.findViewById<CircleImageView>(R.id.profile)
                Picasso.get()
                    .load(foto)
                    .into(panel)
                val name = header.findViewById<TextView>(R.id.name)
                val correo = header.findViewById<TextView>(R.id.email)
                name.text = nombre
                correo.text = email

            }
        })


        materialToolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.itemIconTintList=null
        navigationView.setNavigationItemSelectedListener(object: NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var id = item.itemId
                drawerLayout.closeDrawer(GravityCompat.START)
                when(id){
                    R.id.cerrar -> logOut()
                    R.id.misReportes -> misRpeortes()
                }
                return true
            }
        })

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        nuevoReporte = findViewById(R.id.nuevo)
        nuevoReporte.setOnClickListener(View.OnClickListener {
            arrayDatosUsuario.add(DatosPerfilUsuario(id,nombre,email,foto))
            model.setDatosUsuario(arrayDatosUsuario)
            val nuevoReporte = NuevoReporte()
            detailFraagment(nuevoReporte)
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
        val booleanSuccess = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json))
        if (!booleanSuccess){
            Log.d(TAG, "Style parsing failed.")
        }

        model.getKeysUsers().observe(this, {
            for (keys in it){
                arrayKeysUsuarios.add(keys)
            }

            for (datos in arrayKeysUsuarios){
                Log.d(TAG, "keys: " + datos)
            }
            model.getAltas(it).observe(this, {
                Log.d(TAG, "size: " + it.size)
                for (keys in arrayKeysUsuarios){
                    for(i in 0 until it.size){
                        //Log.d(TAG, "status:45 " + it[i].keys!![i])
                        //Log.d(TAG, "longitud: " + (it[i].reportes!![i].coordenadas?.get(1)))
                        var marker = LatLng(it[i].reportes!![i].coordenadas!!.get(0).toDouble(), it[i].reportes!![i].coordenadas?.get(1)!!.toDouble())

                        when(it[i].reportes!![i].tipo){
                            "Gato" -> markerCasa = mMap.addMarker(MarkerOptions().position(marker).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.gato)).title(keys))
                            "Perro" -> markerCasa = mMap.addMarker(MarkerOptions().position(marker).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.perro)).title(keys))
                            else ->{
                                markerCasa = mMap.addMarker(MarkerOptions().position(marker))
                            }
                        }
                        mMap.isMyLocationEnabled()
                        mMap.setOnMarkerClickListener(this)
                        mHashMap.put(markerCasa,it[i].keys!![i])
                    }
                }
            })
        })
        /*model.getAltas().observe(this, Observer<ArrayList<GetReportes>> {
            Log.d(TAG, "size: " + it.size)
            for(i in 0 until it.size){
                //Log.d(TAG, "status: " + it[i].keys!![i])
                //Log.d(TAG, "longitud: " + (it[i].reportes!![i].coordenadas?.get(1)))
                var marker = LatLng(it[i].reportes!![i].coordenadas!!.get(0).toDouble(), it[i].reportes!![i].coordenadas?.get(1)!!.toDouble())

                when(it[i].reportes!![i].tipo){
                    "Gato" -> markerCasa = mMap.addMarker(MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.gato)))
                    "Perro" -> markerCasa = mMap.addMarker(MarkerOptions().position(marker).icon(BitmapDescriptorFactory.fromResource(R.drawable.perro)))
                    else ->{
                        markerCasa = mMap.addMarker(MarkerOptions().position(marker))
                    }
                }
                mMap.isMyLocationEnabled()
                mMap.setOnMarkerClickListener(this)
                mHashMap.put(markerCasa,it[i].keys!![i])
            }
        })*/
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int
        val id = mHashMap.get(marker)

        Log.d(TAG, "title y id: " + marker.title +" "+id)
        model.getReporteById(marker.title,id.toString())
        //model.setKey(id.toString())
        val detailMarkers = DetailMarker()
        detailFraagment(detailMarkers)
        /*val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)*/

        // Check if a click count was set, then display the click count.
/*        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                this,
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }*/



        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    fun permisoUbicacion() {
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun detailFraagment(fragment: Fragment){
        appbarLayout.visibility = View.GONE
        nuevoReporte.visibility = View.GONE
        val nuevoReporte = fragment
        val ft = supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.map, nuevoReporte)
        // Apply the transaction
        ft?.commit()
    }

    override fun onBackPressed() {

    }

    fun logOut(){
        LoginManager.getInstance().logOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun misRpeortes(){
        appbarLayout.visibility = View.GONE
        var arrayUsuario : ArrayList<DatosPerfilUsuario> = ArrayList()
        arrayUsuario.add(DatosPerfilUsuario(id,nombre,email,foto))
        model.datosPerfilUsuario(arrayUsuario)
        model.setKeyUserMisReportes(id!!)
        val fragmentMisReportes = FragmentMisReportes()
        detailFraagment(fragmentMisReportes)
    }

}