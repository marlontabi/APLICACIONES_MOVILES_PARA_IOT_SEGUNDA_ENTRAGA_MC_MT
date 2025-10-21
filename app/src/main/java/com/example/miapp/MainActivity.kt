package com.example.miapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtFechaHora: TextView
    private lateinit var btnUsuarios: Button
    private lateinit var btnSensores: Button
    private lateinit var btnDesarrollador: Button
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtFechaHora = findViewById(R.id.txtFechaHora)
        btnUsuarios = findViewById(R.id.btnUsuarios)
        btnSensores = findViewById(R.id.btnSensores)
        btnDesarrollador = findViewById(R.id.btnDesarrollador)

        actualizarFechaHora()

        btnUsuarios.setOnClickListener {
            val intent = Intent(this, MenuUsuarios::class.java)
            startActivity(intent)
        }

        btnSensores.setOnClickListener {
            val intent = Intent(this, Sensores::class.java)
            startActivity(intent)
        }

        btnDesarrollador.setOnClickListener {
            val intent = Intent(this, DatosDesarrollador::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarFechaHora() {
        handler.post(object : Runnable {
            override fun run() {
                val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                txtFechaHora.text = formato.format(Date())
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
