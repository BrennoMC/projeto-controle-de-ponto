package com.example.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.text.SimpleDateFormat
import android.widget.TextView;
import java.util.Date
import java.util.Locale
import android.os.Handler
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.controledeponto.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class HomeActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var handler: Handler
    private lateinit var timeUpdater: Runnable
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btnv)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)





        //auth = Firebase.auth

        //textView = findViewById<TextView>(R.id.horario_text)

        //handler = Handler()
/*
        timeUpdater = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime: String = dateFormat.format(Date())

                textView.text = currentTime

                handler.postDelayed(this, 1000)
            }
        }
        */


        //handler.post(timeUpdater)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        val userName = intent.getStringExtra("USER_NAME")

        val userId = intent.getStringExtra("USER_ID")

        val bundle = Bundle()
        bundle.putString("USER_EMAIL", userEmail)
        bundle.putString("USER_NAME", userName)
        bundle.putString("USER_ID", userId)

        /*
        val meuFragment = HomeFragment()
        meuFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, meuFragment)
            .commit()

            */


        //val btnSignOut = findViewById<ImageView>(R.id.btn_sair)

/*
        btnSignOut.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

 */




/*
        val textVIewId = findViewById<TextView>(R.id.textViewMatricula)
        textVIewId.text = "Matrícula: $userId"

        val textViewName = findViewById<TextView>(R.id.textView)
        textViewName.text = "Olá, $userName"

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime: String = dateFormat.format(Date())

        textView.text = currentTime
*/

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeUpdater)
    }

}