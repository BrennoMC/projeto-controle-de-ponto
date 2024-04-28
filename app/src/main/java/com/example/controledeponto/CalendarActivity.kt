package com.example.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Calendar
import android.widget.TextView;
import java.util.Date
import java.util.Locale
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class CalendarActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var handler: Handler
    private lateinit var timeUpdater: Runnable
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        auth = Firebase.auth

        textView = findViewById<TextView>(R.id.horario_text)

        handler = Handler()

        timeUpdater = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime: String = dateFormat.format(Date())

                textView.text = currentTime

                handler.postDelayed(this, 1000)
            }
        }

        handler.post(timeUpdater)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        val userName = intent.getStringExtra("USER_NAME")

        val userId = intent.getStringExtra("USER_ID")

        val btnSignOut = findViewById<ImageView>(R.id.btn_sair)

        btnSignOut.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val textVIewId = findViewById<TextView>(R.id.textViewMatricula)
        textVIewId.text = "Matrícula: $userId"

        val textViewName = findViewById<TextView>(R.id.textView)
        textViewName.text = "Olá, $userName"

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime: String = dateFormat.format(Date())

        textView.text = currentTime
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeUpdater)
    }

}