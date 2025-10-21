package com.example.miapp

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class ModificarEliminar : AppCompatActivity() {

    private lateinit var editNombre: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnVolver: Button
    private lateinit var dbHelper: ConexionDbHelper

    private var idUsuario: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_eliminar)

        editNombre = findViewById(R.id.editNombre)
        editApellidos = findViewById(R.id.editApellidos)
        editEmail = findViewById(R.id.editEmail)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnVolver = findViewById(R.id.btnVolver)
        dbHelper = ConexionDbHelper(this)

        // Recuperar ID de usuario desde el intent
        idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        if (idUsuario != -1) {
            cargarDatosUsuario()
        } else {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudo cargar el usuario.")
                .show()
        }

        // Botón guardar cambios
        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        // Botón eliminar usuario
        btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }

        // Botón volver
        btnVolver.setOnClickListener {
            val intent = Intent(this, ListadoUsuarios::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarDatosUsuario() {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT NOMBRE, APELLIDOS, EMAIL FROM USUARIOS WHERE ID = ?", arrayOf(idUsuario.toString()))

        if (cursor.moveToFirst()) {
            editNombre.setText(cursor.getString(0))
            editApellidos.setText(cursor.getString(1))
            editEmail.setText(cursor.getString(2))
        }
        cursor.close()
        db.close()
    }

    private fun guardarCambios() {
        val nombre = editNombre.text.toString().trim()
        val apellidos = editApellidos.text.toString().trim()
        val email = editEmail.text.toString().trim()

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos vacíos")
                .setContentText("Debes completar todos los campos.")
                .show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Formato inválido")
                .setContentText("El correo ingresado no es válido.")
                .show()
            return
        }

        val db: SQLiteDatabase = dbHelper.writableDatabase

        // Verificar si el correo ya existe (y no es del mismo usuario)
        val cursor = db.rawQuery("SELECT ID FROM USUARIOS WHERE EMAIL = ? AND ID != ?", arrayOf(email, idUsuario.toString()))
        if (cursor.moveToFirst()) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Correo duplicado")
                .setContentText("El correo ya está registrado en otro usuario.")
                .show()
            cursor.close()
            db.close()
            return
        }
        cursor.close()

        val valores = ContentValues().apply {
            put("NOMBRE", nombre)
            put("APELLIDOS", apellidos)
            put("EMAIL", email)
        }

        val filas = db.update("USUARIOS", valores, "ID = ?", arrayOf(idUsuario.toString()))
        db.close()

        if (filas > 0) {
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Éxito")
                .setContentText("Datos actualizados correctamente.")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    val intent = Intent(this, ListadoUsuarios::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        } else {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudieron actualizar los datos.")
                .show()
        }
    }

    private fun confirmarEliminacion() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Eliminar usuario?")
            .setContentText("Esta acción no se puede deshacer.")
            .setConfirmText("Sí, eliminar")
            .setCancelText("Cancelar")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                eliminarUsuario()
            }
            .setCancelClickListener { it.dismissWithAnimation() }
            .show()
    }

    private fun eliminarUsuario() {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val filas = db.delete("USUARIOS", "ID = ?", arrayOf(idUsuario.toString()))
        db.close()

        if (filas > 0) {
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Eliminado")
                .setContentText("Usuario eliminado correctamente.")
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    val intent = Intent(this, ListadoUsuarios::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        } else {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudo eliminar el usuario.")
                .show()
        }
    }
}
