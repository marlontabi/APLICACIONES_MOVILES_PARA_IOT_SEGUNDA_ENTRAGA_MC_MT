package com.example.miapp

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Sensores : AppCompatActivity() {

    private lateinit var datos: RequestQueue
    private val handler = Handler(Looper.getMainLooper())
    private var intervalo = 2000L // 2 segundos por defecto

    private lateinit var txtFecha: TextView
    private lateinit var txtTemp: TextView
    private lateinit var txtHum: TextView
    private lateinit var imgTemp: ImageView
    private lateinit var imgLinterna: ImageView

    private var linternaEncendida = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensores)

        txtFecha = findViewById(R.id.txtFecha)
        txtTemp = findViewById(R.id.txtTemp)
        txtHum = findViewById(R.id.txtHum)
        imgTemp = findViewById(R.id.imgTemp)
        imgLinterna = findViewById(R.id.imgLinterna)

        datos = Volley.newRequestQueue(this)

        imgLinterna.setOnClickListener { alternarLinterna() }

        iniciarActualizacion()
    }

    private fun iniciarActualizacion() {
        handler.post(object : Runnable {
            override fun run() {
                actualizarFecha()
                obtenerDatosSensores()
                handler.postDelayed(this, intervalo)
            }
        })
    }

    private fun actualizarFecha() {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        txtFecha.text = formato.format(Date())
    }

    private fun obtenerDatosSensores() {
        val url = "https://www.pnk.cl/muestra_datos.php"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response: JSONObject ->
                val temperatura = response.optString("temperatura", "")
                val humedad = response.optString("humedad", "")

                txtTemp.text = if (temperatura.isNotEmpty()) "$temperatura °C" else "N/D"
                txtHum.text = if (humedad.isNotEmpty()) "$humedad %" else "N/D"

                val valor = temperatura.toFloatOrNull()
                if (valor != null) actualizarIconoTemperatura(valor)
            },
            { error -> error.printStackTrace() })
        datos.add(request)
    }

    private fun actualizarIconoTemperatura(valor: Float) {
        if (valor > 20f) {
            imgTemp.setImageResource(R.drawable.tempalta)
        } else {
            imgTemp.setImageResource(R.drawable.tempbaja)
        }
    }

    private fun alternarLinterna() {
        val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = camManager.cameraIdList[0]
        try {
            linternaEncendida = !linternaEncendida
            camManager.setTorchMode(cameraId, linternaEncendida)

            if (linternaEncendida) {
                imgLinterna.setImageResource(R.drawable.linternaonn)
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Linterna encendida")
                    .setContentText("La linterna del teléfono está encendida.")
                    .show()
            } else {
                imgLinterna.setImageResource(R.drawable.linternaoff)
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Linterna apagada")
                    .setContentText("La linterna del teléfono está apagada.")
                    .show()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
