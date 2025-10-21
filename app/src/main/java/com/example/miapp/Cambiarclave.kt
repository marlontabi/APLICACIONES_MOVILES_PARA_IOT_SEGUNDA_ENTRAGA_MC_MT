package com.example.miapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import androidx.appcompat.app.AppCompatActivity

class Cambiarclave : AppCompatActivity() {

    private lateinit var nuevaClave: EditText
    private lateinit var confirmarClave: EditText
    private lateinit var btnCambiar: Button
    private var emailUsuario: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiarclave)

        nuevaClave = findViewById(R.id.Nuevaclave)
        confirmarClave = findViewById(R.id.Confirmarclave)
        btnCambiar = findViewById(R.id.btnConfirmar)
        emailUsuario = intent.getStringExtra("EMAIL")

        btnCambiar.setOnClickListener {
            val nueva = nuevaClave.text.toString().trim()
            val confirmar = confirmarClave.text.toString().trim()

            if (!esClaveSegura(nueva)) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Contraseña débil")
                    .setContentText("Debe tener ≥8 caracteres, una mayúscula, una minúscula, un número y un símbolo.")
                    .show()
            } else if (nueva != confirmar) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Las contraseñas no coinciden.")
                    .show()
            } else {
                val db = ConexionDbHelper(this).writableDatabase
                val values = android.content.ContentValues().apply {
                    put("CLAVE", nueva)
                }
                val filas = db.update("USUARIOS", values, "EMAIL = ?", arrayOf(emailUsuario))
                db.close()

                if (filas > 0) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Éxito")
                        .setContentText("Contraseña actualizada correctamente.")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }.show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No se pudo actualizar la contraseña.")
                        .show()
                }
            }
        }
    }

    private fun esClaveSegura(clave: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.])[A-Za-z\\d@\$!%*?&.]{8,}\$")
        return regex.matches(clave)
    }
}
