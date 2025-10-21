package com.example.miapp

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {

    private lateinit var usuarioEdit: EditText
    private lateinit var claveEdit: EditText
    private lateinit var botonLogin: Button
    private lateinit var botonRecuperar: Button
    private lateinit var conexion: ConexionDbHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioEdit = findViewById(R.id.editTextText)
        claveEdit = findViewById(R.id.editTextTextPassword)
        botonLogin = findViewById(R.id.button)
        botonRecuperar = findViewById(R.id.btnRecuperarclav)
        conexion = ConexionDbHelper(this)

        botonLogin.setOnClickListener {
            val usuario = usuarioEdit.text.toString().trim()
            val password = claveEdit.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                consultarUsuario(usuario, password)
            }
        }

        botonRecuperar.setOnClickListener {
            val intent = Intent(this, Recuperarclave::class.java)
            startActivity(intent)
        }
    }

    private fun consultarUsuario(usuario: String, clave: String) {
        val db: SQLiteDatabase = conexion.readableDatabase
        val query = "SELECT * FROM USUARIOS WHERE EMAIL = ? AND CLAVE = ?"
        val cursor = db.rawQuery(query, arrayOf(usuario, clave))

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }

        cursor.close()
        db.close()
    }
}
