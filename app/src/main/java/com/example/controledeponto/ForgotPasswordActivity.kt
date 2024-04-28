package com.example.controledeponto

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        auth = Firebase.auth

        val btnForgotPassword = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_forgotPassword)

        btnForgotPassword.setOnClickListener {
            val email = findViewById<EditText>(R.id.forgotPasswordEmail).text.toString()

            if (email.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    val emailCut = email.split("@")
                    val emailDomain = emailCut[1]
                    val emailModel = "puccampinas.edu.br"

                    if(emailDomain == emailModel) {
                        SendPasswordResetEmail(email)
                    } else {
                        Toast.makeText(baseContext, "Não é um email PUC", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(baseContext, "Não é um email valido", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(baseContext, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun SendPasswordResetEmail(email: String) {

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email de redefinição enviado com sucesso
                    Log.d("Reset Password", "Email enviado")
                    Toast.makeText(baseContext, "Email enviado", Toast.LENGTH_LONG).show()
                } else {
                    // Falha ao enviar email de redefinição
                    Log.w("Reset Password", "Falha ao enviar email: ${task.exception}")
                    Toast.makeText(baseContext, "Falha ao enviar email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }


    }
}