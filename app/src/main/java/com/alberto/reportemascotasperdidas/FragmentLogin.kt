package com.alberto.reportemascotasperdidas

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.alberto.reportemascotasperdidas.Models.DatosPerfilUsuario
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import java.util.*

class FragmentLogin : Fragment() {

    private val TAG = "Login"
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginFacebbok: MaterialButton
    private var arrayDatosUsuario: ArrayList<DatosPerfilUsuario> = ArrayList()
    private val model: VMDatos by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        loginFacebbok = view.findViewById(R.id.facebook)

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

        return view
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

                    arrayDatosUsuario.add(DatosPerfilUsuario(id,nombre,email,foto))
                    val mapsFragment = FragmentMaps()
                    openFraagment(mapsFragment)
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        request.parameters = parameters
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