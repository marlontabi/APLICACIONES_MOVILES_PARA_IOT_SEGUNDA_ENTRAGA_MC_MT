package com.example.miapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog

class ValidarCodigo : AppCompatActivity() {

    private lateinit var txtCodigo: EditText
    private lateinit var btnVerificar: Button
    private lateinit var dbHelper: ConexionDbHelper
    private var emailUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validar_codigo)

        txtCodigo = findViewById(R.id.txtCodigo)
        btnVerificar = findViewById(R.id.btnValidarCodigo)
        dbHelper = ConexionDbHelper(this)

        emailUsuario = intent.getStringExtra("EMAIL")

        btnVerificar.setOnClickListener {
            val codigoIngresado = txtCodigo.text.toString().trim()
            if (codigoIngresado.isEmpty() || !codigoIngresado.all { it.isDigit() }) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Código inválido")
                    .setContentText("Ingrese el código numérico de 5 dígitos.")
                    .show()
                return@setOnClickListener
            }

            if (emailUsuario == null) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No se recibió el email.")
                    .show()
                return@setOnClickListener
            }

            validarCodigo(emailUsuario!!, codigoIngresado.toInt())
        }
    }

    private fun validarCodigo(email: String, codigo: Int) {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT CODIGO, EXPIRA FROM CODIGOS_RECUPERACION WHERE EMAIL = ? ORDER BY ID DESC LIMIT 1",
            arrayOf(email)
        )

        if (cursor.moveToFirst()) {
            val codigoBD = cursor.getInt(0)
            val expira = cursor.getLong(1)
            cursor.close()
            db.close()

            val ahora = System.currentTimeMillis()
            if (ahora > expira) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Código caducado")
                    .setContentText("El código ha expirado. Solicita uno nuevo.")
                    .show()
                return
            }

            if (codigoBD == codigo) {
                // válido -> abrir pantalla cambiar clave (ya la tienes: Cambiarclave)
                val intent = Intent(this, Cambiarclave::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish()
            } else {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Código incorrecto")
                    .setContentText("El código ingresado no coincide.")
                    .show()
            }
        } else {
            cursor.close()
            db.close()
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No hay código")
                .setContentText("No se encontró ningún código para este correo. Genera uno primero.")
                .show()
        }
    }
}
