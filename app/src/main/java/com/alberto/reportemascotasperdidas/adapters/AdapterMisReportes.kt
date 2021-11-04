package com.alberto.reportemascotasperdidas.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alberto.reportemascotasperdidas.Models.GetReportes
import com.alberto.reportemascotasperdidas.Models.Reportes
import com.alberto.reportemascotasperdidas.R
import com.google.android.material.imageview.ShapeableImageView
import com.limerse.slider.adapter.FiniteCarouselAdapter
import com.squareup.picasso.Picasso
import java.lang.Exception

class AdapterMisReportes(private val arrayReportes: ArrayList<GetReportes>) : RecyclerView.Adapter<AdapterMisReportes.MyViewHolder>(){

    private var TAG = "AdapterReportes"
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: String)
    }

    fun setOnItemClickListener(lister: onItemClickListener){
        mListener = lister
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_mis_reportes,parent, false)
        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = arrayReportes

        try {
            Picasso.get()
                .load(currentItem[position].reportes!![position].arrayImagenes!!.get(0))
                .into(holder.foto)
        }catch (ex:Exception){

        }
        holder.tipo.text = currentItem[position].reportes!![position].tipo
        holder.tamano.text = currentItem[position].reportes!![position].tamano
        holder.fecha.text = currentItem[position].reportes!![position].fecha
        holder.hora.text = currentItem[position].reportes!![position].hm
        holder.idReporte.text = currentItem[position].keys!![position]
        Log.d(TAG, "keys: " + currentItem[position].keys!![position])
    }

    override fun getItemCount(): Int {
        return arrayReportes.size
    }

    class MyViewHolder(itemView: View, lister: onItemClickListener): RecyclerView.ViewHolder(itemView){
        val foto: ShapeableImageView = itemView.findViewById(R.id.imageList)
        val tipo: TextView = itemView.findViewById(R.id.tipo)
        val tamano: TextView = itemView.findViewById(R.id.tamano)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val hora: TextView = itemView.findViewById(R.id.hora)
        val idReporte: TextView = itemView.findViewById(R.id.idReporte)

        init {
            itemView.setOnClickListener {
                lister.onItemClick(idReporte.text.toString())
            }
        }
    }
}