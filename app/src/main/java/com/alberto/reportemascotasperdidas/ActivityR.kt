package com.alberto.reportemascotasperdidas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActivityR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r)

        val nuevoReporte = NuevoReporte()
        val ft = supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.formulario, nuevoReporte)
        // Apply the transaction
        ft?.commit()
    }
}