package com.example.miapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuUsuarios : AppCompatActivity() {

    private lateinit var btnIngresar: Button
    private lateinit var btnListar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_usuarios)

        btnIngresar = findViewById(R.id.btnIngresarUsuario)
        btnListar = findViewById(R.id.btnListarUsuarios)

        btnIngresar.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        btnListar.setOnClickListener {
            val intent = Intent(this, ListadoUsuarios::class.java)
            startActivity(intent)
        }
    }
}
