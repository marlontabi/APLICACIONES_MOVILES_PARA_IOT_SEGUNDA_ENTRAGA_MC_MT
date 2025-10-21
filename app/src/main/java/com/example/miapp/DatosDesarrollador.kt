package com.example.miapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DatosDesarrollador : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_desarrollador)

        val github = findViewById<ImageView>(R.id.iconGitHub1)
        val github1 = findViewById<ImageView>(R.id.iconGitHub2)

        github.setOnClickListener {
            abrirEnlace("https://github.com/marlontabi/APLICACIONES_MOVILES_PARA_IOT_SEGUNDA_ENTRAGA_MC_MT")
        }
        github1.setOnClickListener {
            abrirEnlace("https://github.com/marlontabi/APLICACIONES_MOVILES_PARA_IOT_SEGUNDA_ENTRAGA_MC_MT")
        }
    }

    private fun abrirEnlace(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
