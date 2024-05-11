package com.example.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val btnLogin = findViewById<AppCompatButton>(R.id.btn_login)

        val btnRegister = findViewById<TextView>(R.id.txtRegister)

        val btnForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)

        /*btnLogin.setOnClickListener {
            val goToMenu = Intent(this@MainActivity, HomeActivity::class.java)

            startActivity(goToMenu)
        }*/

        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.loginEmail).text.toString()
            val password = findViewById<EditText>(R.id.loginPassword).text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //Toast.makeText(baseContext, "É um email valido", Toast.LENGTH_LONG).show()

                    val emailCut = email.split("@")
                    val emailDomain = emailCut[1]
                    val emailModel = "puccampinas.edu.br"

                    if(emailDomain == emailModel) {
                        signInWithEmailAndPassword(email, password)
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

        btnRegister.setOnClickListener {
            val goToRegister = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(goToRegister)
        }

        btnForgotPassword.setOnClickListener {
            val goToForgotPassword = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(goToForgotPassword)
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d(TAG, "singInWithEmailAndPassword:Sucess")
                val user = auth.currentUser
                Log.d(TAG, "$user")

                val goToMenu = Intent(this@MainActivity, HomeActivity::class.java)

                goToMenu.putExtra("USER_EMAIL", user?.email)
                goToMenu.putExtra("USER_NAME", user?.displayName)
                goToMenu.putExtra("USER_ID",user?.uid)

                /*val bundle = Bundle()
                bundle.putString("USER_EMAIL", user?.email)
                bundle.putString("USER_NAME", user?.displayName)
                bundle.putString("USER_ID", user?.uid)

                val meuFragment = HomeFragment()
                meuFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, meuFragment)
                    .commit()*/

                startActivity(goToMenu)

            } else {
                if (task.exception?.message == "The supplied auth credential is incorrect, malformed or has expired.") {
                    Toast.makeText(baseContext, "As informações fornecidas estão incorretas", Toast.LENGTH_LONG).show()
                } else {
                    Log.w(TAG, "singInWithEmailAndPassword:Sucess", task.exception)
                    Toast.makeText(baseContext, "Authenication Failure", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }
}