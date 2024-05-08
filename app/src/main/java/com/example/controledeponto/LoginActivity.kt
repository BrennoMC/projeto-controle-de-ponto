package com.example.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val btnLogin = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.loginEmail).text.toString()
            val password = findViewById<EditText>(R.id.loginPassword).text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
                val goToMenu = Intent(this@LoginActivity, HomeActivity::class.java)

                val user = auth.currentUser

                goToMenu.putExtra("USER_EMAIL", user?.email)
                goToMenu.putExtra("USER_NAME", user?.displayName)
                goToMenu.putExtra("USER_ID", user?.providerId)

                startActivity(goToMenu)
            } else {
                Toast.makeText(baseContext, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d(TAG, "singInWithEmailAndPassword:Sucess")
                val user = auth.currentUser
                Log.d(TAG, "$user")

                Toast.makeText(baseContext, "Logado com Sucesso! Usuário: $user", Toast.LENGTH_LONG).show()


            } else {
                Log.w(TAG, "singInWithEmailAndPassword:Sucess", task.exception)
                Toast.makeText(baseContext, "Authenication Failure", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }
}