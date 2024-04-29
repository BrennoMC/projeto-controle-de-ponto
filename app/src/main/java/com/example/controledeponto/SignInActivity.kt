package com.example.controledeponto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import java.util.logging.Handler

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        val btnRegister = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_signIn)

        btnRegister.setOnClickListener {

            val userName = findViewById<EditText>(R.id.registerName).text.toString()
            val userEmail = findViewById<EditText>(R.id.registerEmail).text.toString()
            val userPassword = findViewById<EditText>(R.id.registerPassword).text.toString()
            val userConfirmPassword = findViewById<EditText>(R.id.registerConfirmPassword).text.toString()

            if(userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty() && userConfirmPassword.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {

                    val emailCut = userEmail.split("@")
                    val emailDomain = emailCut[1]
                    val emailModel = "puccampinas.edu.br"

                    if(emailDomain == emailModel) {
                        if(userPassword.length >= 6 || userConfirmPassword.length >= 6) {
                            if (userPassword == userConfirmPassword) {
                                createUserWithEmailAndPassword(userEmail, userPassword, userName)
                            } else {
                                Toast.makeText(baseContext, "Senhas não são iguais", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(baseContext, "A senha precisa ter no mínino 6 dígitos", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "Não é um email PUC", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(baseContext, "Não é um email valido", Toast.LENGTH_LONG).show()
                }
            }

        }



    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String) {

        val name = name

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.d(TAG, "$user")

                    if (user != null) {
                        val newDisplayName = name  // Substitua pelo nome desejado

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(newDisplayName)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Perfil", "Perfil do usuário atualizado com sucesso!")
                                } else {
                                    Log.w("Perfil", "Erro ao atualizar o perfil do usuário", task.exception)
                                }
                            }
                    }
                    Toast.makeText(baseContext, "Usuário cadastrado com sucesso.", Toast.LENGTH_SHORT,).show()
                    Toast.makeText(baseContext, "Você será redirecionado para o login", Toast.LENGTH_SHORT,).show()

                    val handler = android.os.Handler()

                    val runnable = Runnable {
                        val goToLogin = Intent(this, MainActivity::class.java)
                        startActivity(goToLogin)
                    }

                    handler.postDelayed(runnable, 5000)

                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if(task.exception?.message == "The email address is already in use by another account.") {
                        Toast.makeText(baseContext, "Este email já está cadastrado", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT,).show()
                    }
                }
            }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }
}