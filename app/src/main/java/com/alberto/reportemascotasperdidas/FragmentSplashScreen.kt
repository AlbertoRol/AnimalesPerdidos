package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import org.json.JSONObject

class FragmentSplashScreen : Fragment() {

    private val TAG = "FragmentSplash"
    private lateinit var imgSplash: ImageView
    private lateinit var lottieAnimationView: LottieAnimationView
    private var arrayDatosUsuario: ArrayList<DatosPerfilUsuario> = ArrayList()
    private val model: VMDatos by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_splash_screen, container, false)

        imgSplash = view.findViewById(R.id.splash)
        lottieAnimationView = view.findViewById(R.id.lottie)

        imgSplash.animate().translationY(-2100f).setDuration(1000).startDelay = 3000
        lottieAnimationView.animate().translationY(1400f).setDuration(1000).startDelay = 3000

        Handler().postDelayed({
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            if (isLoggedIn){
                requestFacebook()
            }else{
                val fragmentLogin = FragmentLogin()
                openFraagment(fragmentLogin)
            }
        }, 4000)


        return view
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

                    arrayDatosUsuario.add(DatosPerfilUsuario(id,nombre,email,foto))
                    val mapsFragment = FragmentMaps()
                    openFraagment(mapsFragment)
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        request.parameters =  parameters
        request.executeAsync()
    }


    fun openFraagment(fragment: Fragment){
        model.datosPerfilUsuario(arrayDatosUsuario)
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, fragment)
        // Apply the transaction
        ft?.commit()
    }

}