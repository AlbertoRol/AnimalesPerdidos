package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import org.json.JSONObject

class SplashScreen : AppCompatActivity() {

    private val TAG = "SplashScreen"
    private lateinit var imgSplash: ImageView
    private lateinit var lottieAnimationView: LottieAnimationView
    private var arrayDatosUsuario: ArrayList<DatosPerfilUsuario> = ArrayList()
    private val model: VMDatos by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashScreen = FragmentSplashScreen()
        val ft = supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, splashScreen)
        // Apply the transaction
        ft?.commit()
    }

    fun requestFacebook(){
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(), object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                    Log.d(TAG, "id: ${response!!.getJSONObject()!!.get("id")}")
                    Log.d(TAG, "nombre: ${response!!.getJSONObject()!!.get("name")}")
                    Log.d(TAG, "email: ${response!!.getJSONObject()!!.get("email")}")
                    Log.d(TAG, "foto: " + response!!.getJSONObject()!!.getJSONObject("picture").getJSONObject("data").get("url"))
                    var id = response!!.getJSONObject()!!.get("id").toString()
                    var nombre = response!!.getJSONObject()!!.get("name").toString()
                    var email = response!!.getJSONObject()!!.get("email").toString()
                    var foto = response!!.getJSONObject()!!.getJSONObject("picture").getJSONObject("data").get("url").toString()

                    startMain(id,nombre,email,foto)
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        request.parameters =  parameters
        request.executeAsync()
    }

    fun startMain(id:String, nombre:String, email:String, foto:String){
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("nombre", nombre)
        intent.putExtra("email", email)
        intent.putExtra("foto", foto)
        val options = ActivityOptions
            .makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
        startActivity(intent, options.toBundle())

    }

    override fun onBackPressed() {

    }
}