package com.example.miapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class ListadoUsuarios : AppCompatActivity() {

    private lateinit var listaUsuarios: ListView
    private lateinit var txtBuscar: EditText
    private lateinit var btnVolver: Button
    private lateinit var dbHelper: ConexionDbHelper
    private lateinit var adaptador: ArrayAdapter<String>
    private val usuarios = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_usuarios)

        listaUsuarios = findViewById(R.id.listaUsuarios)
        txtBuscar = findViewById(R.id.txtBuscar)
        btnVolver = findViewById(R.id.btn_volver_listado)
        dbHelper = ConexionDbHelper(this)

        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, usuarios)
        listaUsuarios.adapter = adaptador

        cargarUsuarios()

        txtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptador.filter.filter(s.toString())
            }
        })

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarUsuarios() {
        usuarios.clear()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT NOMBRE, APELLIDOS, EMAIL FROM USUARIOS", null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val apellidos = cursor.getString(1)
                val email = cursor.getString(2)
                usuarios.add("$nombre $apellidos\n$email")
            } while (cursor.moveToNext())
        } else {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sin usuarios")
                .setContentText("No hay usuarios registrados.")
                .show()
        }

        cursor.close()
        db.close()
        adaptador.notifyDataSetChanged()
    }
}
