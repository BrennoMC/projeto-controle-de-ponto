package com.example.controledeponto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity: AppCompatActivity() {
    private  var time: Long = 3000
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()

        Handler().postDelayed({

            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)

            finish()
        }, time)
    }
}
