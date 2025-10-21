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

        idUsuario = intent.getIntExtra("USUARIO_ID", -1)

        if (idUsuario == -1) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se pudo cargar el usuario.")
                .show()
            finish()
            return
        } else {
            cargarDatosUsuario()
        }

        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { confirmarEliminacion() }
        btnVolver.setOnClickListener { finish() }
    }

    private fun cargarDatosUsuario() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT NOMBRE, APELLIDOS, EMAIL FROM USUARIOS WHERE ID = ?",
            arrayOf(idUsuario.toString())
        )

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

        when {
            nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() -> {
                mostrarAlerta("Campos vacíos", "Debes completar todos los campos.", SweetAlertDialog.WARNING_TYPE)
                return
            }

            !nombre.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) -> {
                mostrarAlerta("Nombre inválido", "El nombre solo puede contener letras y espacios.", SweetAlertDialog.ERROR_TYPE)
                return
            }

            !apellidos.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) -> {
                mostrarAlerta("Apellidos inválidos", "Los apellidos solo pueden contener letras y espacios.", SweetAlertDialog.ERROR_TYPE)
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                mostrarAlerta("Correo inválido", "El formato del correo electrónico no es válido.", SweetAlertDialog.ERROR_TYPE)
                return
            }
        }

        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT ID FROM USUARIOS WHERE EMAIL = ? AND ID != ?",
            arrayOf(email, idUsuario.toString())
        )
        if (cursor.moveToFirst()) {
            mostrarAlerta("Correo duplicado", "El correo ya está registrado en otro usuario.", SweetAlertDialog.WARNING_TYPE)
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
            mostrarAlerta("Éxito", "Datos actualizados correctamente.", SweetAlertDialog.SUCCESS_TYPE) {
                finish()
            }
        } else {
            mostrarAlerta("Error", "No se pudieron actualizar los datos.", SweetAlertDialog.ERROR_TYPE)
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
        val db = dbHelper.writableDatabase
        val filas = db.delete("USUARIOS", "ID = ?", arrayOf(idUsuario.toString()))
        db.close()

        if (filas > 0) {
            mostrarAlerta("Eliminado", "Usuario eliminado correctamente.", SweetAlertDialog.SUCCESS_TYPE) {
                finish()
            }
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el usuario.", SweetAlertDialog.ERROR_TYPE)
        }
    }


    private fun mostrarAlerta(titulo: String, mensaje: String, tipo: Int, onConfirm: (() -> Unit)? = null) {
        val alert = SweetAlertDialog(this, tipo)
            .setTitleText(titulo)
            .setContentText(mensaje)
        if (onConfirm != null) {
            alert.setConfirmClickListener {
                it.dismissWithAnimation()
                onConfirm()
            }
        }
        alert.show()
    }
}
