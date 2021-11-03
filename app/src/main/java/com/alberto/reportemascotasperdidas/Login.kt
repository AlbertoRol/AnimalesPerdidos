package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton
import java.util.*

import com.facebook.login.LoginResult

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.airbnb.lottie.LottieAnimationView
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.*
import com.facebook.GraphResponse
import org.json.JSONObject
import com.facebook.login.LoginManager

import com.facebook.AccessToken
import com.google.android.material.button.MaterialButton
import com.facebook.FacebookException

import com.facebook.FacebookCallback
import kotlin.collections.ArrayList


class Login : AppCompatActivity() {

    private val TAG = "Login"
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginFacebbok: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginFacebbok = findViewById(R.id.facebook)

        callbackManager = CallbackManager.Factory.create();

        loginFacebbok.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    Log.d(TAG, "fain :)")
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    Log.d(TAG, "mal :(")
                }
            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
        requestFacebook()
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
        request.parameters = parameters
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
}