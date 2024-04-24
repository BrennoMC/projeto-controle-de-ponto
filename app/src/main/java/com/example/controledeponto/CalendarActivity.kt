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
import android.widget.ImageView

class CalendarActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var handler: Handler
    private lateinit var timeUpdater: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

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

        val btn = findViewById<ImageView>(R.id.btn_sair)

        btn.setOnClickListener {
            val goToMain = Intent(this, MainActivity::class.java)
            startActivity(goToMain)
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