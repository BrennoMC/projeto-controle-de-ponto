package com.example.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.Calendar
import android.widget.TextView;
import java.util.Date
import java.util.Locale
import android.os.Handler
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

        val calendar = Calendar.getInstance()

        val calendarBg = resources.getDrawable(R.drawable.calendar_border_white)

        val btnSignOut = findViewById<ImageView>(R.id.btn_sair)

        auth = Firebase.auth

        btnSignOut.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val days = listOf(
            R.id.segunda_number,
            R.id.terca_number,
            R.id.quarta_number,
            R.id.quinta_number,
            R.id.sexta_number
        )

        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val todayIndex = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
        val maxDaysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        findViewById<TextView>(when (dayOfWeek) {
            Calendar.MONDAY -> R.id.calendars
            Calendar.TUESDAY -> R.id.calendars2
            Calendar.WEDNESDAY -> R.id.calendars3
            Calendar.THURSDAY -> R.id.calendars4
            Calendar.FRIDAY -> R.id.calendars5
            else -> R.id.calendars
        }).apply {
            background = calendarBg
        }
        for ((index, textViewId) in days.withIndex()) {
            var dayOfMonth = currentDayOfMonth - (todayIndex - index)
            if (dayOfMonth > maxDaysInCurrentMonth) {
                dayOfMonth -= maxDaysInCurrentMonth
                calendar.add(Calendar.MONTH, 1)
            }
            findViewById<TextView>(textViewId).text = dayOfMonth.toString()
        }

        when(calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                findViewById<TextView>(R.id.segunda_text).apply {
                    setTextColor(getColor(R.color.black))
                }
                findViewById<TextView>(R.id.segunda_number).apply {
                    setTextColor(getColor(R.color.black))
                }
            }
            Calendar.TUESDAY -> {
                findViewById<TextView>(R.id.terca_text).apply {
                    setTextColor(getColor(R.color.black))
                }
                findViewById<TextView>(R.id.terca_number).apply {
                    setTextColor(getColor(R.color.black))
                }

            }
            Calendar.WEDNESDAY -> {
                findViewById<TextView>(R.id.quarta_text).apply {
                    setTextColor(getColor(R.color.black))
                }
                findViewById<TextView>(R.id.quarta_number).apply {
                    setTextColor(getColor(R.color.black))
                }
            }
            Calendar.THURSDAY -> {
                findViewById<TextView>(R.id.quinta_text).apply {
                    setTextColor(getColor(R.color.black))
                }
                findViewById<TextView>(R.id.quinta_number).apply {
                    setTextColor(getColor(R.color.black))
                }

            }
            Calendar.FRIDAY -> {
                findViewById<TextView>(R.id.sexta_text).apply {
                    setTextColor(getColor(R.color.black))
                }
                findViewById<TextView>(R.id.sexta_number).apply {
                    setTextColor(getColor(R.color.black))
                }
            }
        }

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