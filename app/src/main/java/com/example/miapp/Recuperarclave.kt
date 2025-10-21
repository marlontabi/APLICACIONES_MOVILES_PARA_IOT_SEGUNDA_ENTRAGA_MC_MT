package com.example.miapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlin.concurrent.thread

class Recuperarclave : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var btnRecuperar: Button
    private val dbHelper by lazy { ConexionDbHelper(this) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_clave)

        txtEmail = findViewById(R.id.txtEmailRecuperar)
        btnRecuperar = findViewById(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {
            val email = txtEmail.text.toString().trim()

            if (email.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Campo vacío")
                    .setContentText("Por favor, ingresa tu correo electrónico.")
                    .show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Formato inválido")
                    .setContentText("El correo ingresado no es válido.")
                    .show()
                return@setOnClickListener
            }

            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE EMAIL = ?", arrayOf(email))

            if (cursor.moveToFirst()) {
                cursor.close()
                db.close()
                generarYEnviarCodigo(email)
            } else {
                cursor.close()
                db.close()
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("No encontrado")
                    .setContentText("El correo no está registrado.")
                    .show()
            }
        }
    }

    private fun generarYEnviarCodigo(email: String) {
        val codigo = (10000..99999).random()
        val expiracion = System.currentTimeMillis() + 60_000 // 1 minuto

        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM CODIGOS_RECUPERACION WHERE EMAIL = ?", arrayOf(email))
        db.execSQL(
            "INSERT INTO CODIGOS_RECUPERACION (EMAIL, CODIGO, EXPIRA) VALUES (?, ?, ?)",
            arrayOf(email, codigo, expiracion)
        )
        db.close()

        val progress = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Enviando correo...")
        progress.show()

        thread {
            try {
                val asunto = "Código de recuperación"
                val cuerpo = """
                    Hola,
                    
                    Tu código de recuperación es: $codigo
                    (Válido por 1 minuto)
                    
                    Atentamente,
                    Equipo MiApp
                """.trimIndent()

                // ✉️ Enviar el correo con tu MailSender real
                MailSender.send(email, asunto, cuerpo)

                Handler(Looper.getMainLooper()).post {
                    progress.dismissWithAnimation()
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Correo enviado")
                        .setContentText("Revisa tu bandeja de entrada o carpeta de spam.")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            val intent = Intent(this, ValidarCodigo::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()
                        }
                        .show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    progress.dismissWithAnimation()
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error al enviar correo")
                        .setContentText("No se pudo enviar el correo. Verifica la conexión o tu configuración SMTP.")
                        .show()
                }
            }
        }
    }
}
