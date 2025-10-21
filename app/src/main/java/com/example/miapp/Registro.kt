package com.example.miapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import androidx.appcompat.app.AppCompatActivity

class Registro : AppCompatActivity() {

    private lateinit var nombreEdit: EditText
    private lateinit var apellidosEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var claveEdit: EditText
    private lateinit var confirmarEdit: EditText
    private lateinit var botonRegistrar: Button
    private lateinit var conexion: ConexionDbHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        nombreEdit = findViewById(R.id.txtNombre)
        apellidosEdit = findViewById(R.id.txtApellidos)
        emailEdit = findViewById(R.id.txtEmail)
        claveEdit = findViewById(R.id.txtClave)
        confirmarEdit = findViewById(R.id.txtConfirmar)
        botonRegistrar = findViewById(R.id.btnRegistrar)
        conexion = ConexionDbHelper(this)

        botonRegistrar.setOnClickListener {
            val nombre = nombreEdit.text.toString().trim()
            val apellidos = apellidosEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val clave = claveEdit.text.toString().trim()
            val confirmar = confirmarEdit.text.toString().trim()

            when {
                nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || clave.isEmpty() || confirmar.isEmpty() -> {
                    alerta("Campos obligatorios", "Debe completar todos los campos.", SweetAlertDialog.WARNING_TYPE)
                }
                !soloLetras(nombre) -> {
                    alerta("Nombre inválido", "El nombre no puede contener números ni símbolos.", SweetAlertDialog.ERROR_TYPE)
                }
                !soloLetras(apellidos) -> {
                    alerta("Apellido inválido", "El apellido no puede contener números ni símbolos.", SweetAlertDialog.ERROR_TYPE)
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    alerta("Formato inválido", "Ingrese un correo electrónico válido.", SweetAlertDialog.ERROR_TYPE)
                }
                !esClaveSegura(clave) -> {
                    alerta("Contraseña débil", "Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 símbolo.", SweetAlertDialog.WARNING_TYPE)
                }
                clave != confirmar -> {
                    alerta("No coincide", "Las contraseñas deben ser idénticas.", SweetAlertDialog.ERROR_TYPE)
                }
                else -> registrarUsuario(nombre, apellidos, email, clave)
            }
        }
    }


    private fun soloLetras(texto: String): Boolean {
        val regex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")
        return regex.matches(texto)
    }


    private fun esClaveSegura(clave: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.])[A-Za-z\\d@\$!%*?&.]{8,}\$")
        return regex.matches(clave)
    }


    private fun registrarUsuario(nombre: String, apellidos: String, email: String, clave: String) {
        val db: SQLiteDatabase = conexion.writableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM USUARIOS WHERE EMAIL = ?", arrayOf(email))

        if (cursor.moveToFirst()) {
            alerta("Email existente", "Ya existe una cuenta registrada con ese correo.", SweetAlertDialog.ERROR_TYPE)
            cursor.close()
            db.close()
            return
        }
        cursor.close()

        try {
            val valores = ContentValues().apply {
                put("NOMBRE", nombre)
                put("APELLIDOS", apellidos)
                put("EMAIL", email)
                put("CLAVE", clave)
            }
            val resultado = db.insert("USUARIOS", null, valores)
            db.close()

            if (resultado != -1L) {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Registro exitoso")
                    .setContentText("Tu cuenta ha sido creada correctamente.")
                    .setConfirmClickListener {
                        it.dismissWithAnimation()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }.show()
            } else {
                alerta("Error del servidor", "No se pudo registrar el usuario. Intente nuevamente.", SweetAlertDialog.ERROR_TYPE)
            }
        } catch (e: Exception) {
            alerta("Error inesperado", e.message ?: "Ocurrió un error desconocido.", SweetAlertDialog.ERROR_TYPE)
        }
    }

    private fun alerta(titulo: String, mensaje: String, tipo: Int) {
        SweetAlertDialog(this, tipo)
            .setTitleText(titulo)
            .setContentText(mensaje)
            .show()
    }
}
