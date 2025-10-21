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
    private val items = mutableListOf<String>()     // lo que se muestra
    private val ids = mutableListOf<Int>()          // IDs alineados por posición
    private val emails = mutableListOf<String>()    // opcional, por si lo necesitas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_usuarios)

        listaUsuarios = findViewById(R.id.listaUsuarios)
        txtBuscar = findViewById(R.id.txtBuscar)
        btnVolver = findViewById(R.id.btn_volver_listado)
        dbHelper = ConexionDbHelper(this)

        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listaUsuarios.adapter = adaptador

        cargarUsuarios()

        // Filtro en tiempo real
        txtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptador.filter.filter(s.toString())
            }
        })

        // Volver
        btnVolver.setOnClickListener {
            finish()
        }

        // Abrir Modificar/Eliminar al tocar un usuario
        listaUsuarios.setOnItemClickListener { parent, view, position, id ->
            // OJO: si hay filtro activo, la posición del adaptador filtrado puede no coincidir
            // con nuestra lista original. Obtenemos el texto clicado y buscamos su índice real.
            val textoSeleccionado = parent.getItemAtPosition(position) as String
            val idxReal = items.indexOf(textoSeleccionado)
            if (idxReal >= 0) {
                val userId = ids[idxReal]
                val intent = Intent(this, ModificarEliminar::class.java)
                intent.putExtra("USUARIO_ID", userId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se pudo abrir el usuario seleccionado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarUsuarios() {
        items.clear()
        ids.clear()
        emails.clear()

        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ID, NOMBRE, APELLIDOS, EMAIL FROM USUARIOS ORDER BY NOMBRE ASC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val apellidos = cursor.getString(2)
                val email = cursor.getString(3)

                ids.add(id)
                emails.add(email)
                items.add("$nombre $apellidos\n$email")
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

    override fun onResume() {
        super.onResume()
        // refresca la lista al volver de Modificar/Eliminar
        cargarUsuarios()
    }
}