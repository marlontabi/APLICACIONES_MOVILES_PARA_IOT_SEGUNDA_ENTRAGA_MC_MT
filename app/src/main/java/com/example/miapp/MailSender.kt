package com.example.miapp

import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object MailSender {

    private const val username = "deadbicicle123@gmail.com"
    private const val password = "cvrj oujo fslx cphi"

    fun send(to: String, subject: String, body: String) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject, "UTF-8")
                setText(body, "UTF-8")
            }

            Transport.send(message)
            println("✅ Correo enviado correctamente a $to")

        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Error al enviar el correo: ${e.message}")
            throw RuntimeException("Error al enviar el correo: ${e.message}")
        }
    }
}
