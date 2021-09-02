package com.alberto.reportemascotasperdidas.Models

import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FormatoFecha {

    val TAG = "DateFormat"

    fun formatDate(date: Long): String{

        var fecha_formato = ""

        try {

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(date)

            //Fromato dia
            val format: DateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dia_formato: String = format.format(calendar.time)
            val dia = dia_formato.substring(0,1).uppercase() + dia_formato.substring(1)

            val dia_mes = calendar.get(Calendar.DAY_OF_MONTH)
            val mes = calendar.get(Calendar.MONTH) + 1
            val ano = calendar.get(Calendar.YEAR)


            fecha_formato = dia+" "+dia_mes + " de " + nombreMes(mes) + " del " + ano


            Log.d(TAG, "dia: " +dia_mes)
            Log.d(TAG, "mes: " +mes)
            Log.d(TAG, "ano: " +ano)

        }catch (exception: Exception){
            Log.d(TAG, "formatDate: ")
        }
        return fecha_formato
    }

    fun nombreMes(ano: Int): String{
        val mes = when(ano){
            1 ->  "enero"
            2 -> "febrero"
            3 -> "marzo"
            4 -> "abril"
            5 -> "mayo"
            6 -> "junio"
            7 -> "julio"
            8 -> "agosto"
            9 -> "septiembre"
            10 -> "octubre"
            11 -> "noviembre"
            12 -> "diciembre"
            else -> "sin mes"
        }
        return mes
    }
}