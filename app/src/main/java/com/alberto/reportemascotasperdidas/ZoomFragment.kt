package com.alberto.reportemascotasperdidas

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.alberto.reportemascotasperdidas.ViewModel.VMDatos
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso

class ZoomFragment : Fragment() {

    private val TAG = "ZoomImage"
    private val model: VMDatos by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_zoom, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.tool)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar.title = " "
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val detailMarker = DetailMarker()
            backDetailFragment(detailMarker)
        }

        val img = view.findViewById<TouchImageView>(R.id.imageView)

        model.urlImage.observe(viewLifecycleOwner, { url ->
            Log.d(TAG, "url: " + url)
            Picasso.get()
                .load(url)
                .into(img)
        })

        return view
    }

    fun backDetailFragment(fragment:Fragment){
        val nuevoReporte = fragment
        val ft = activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,  // popEnter
                R.anim.slide_out // popExit
            )
            ?.replace(R.id.frame, nuevoReporte)
        // Apply the transaction
        ft?.commit()
    }

}