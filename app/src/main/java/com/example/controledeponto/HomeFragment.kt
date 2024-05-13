package com.example.controledeponto

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.controledeponto.databinding.ActivityHomeBinding
import com.example.controledeponto.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.PolyUtil
import java.time.LocalTime
import java.util.Date
import java.util.Locale

import android.content.Intent
import android.widget.ImageView
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private val polygonPoints = listOf(
        LatLng(-22.83634805986867, -47.05268883988106),
        LatLng(-22.831744750589777, -47.05340783033304),
        LatLng(-22.83059176664599, -47.04440597451111),
        LatLng(-22.832080015670517, -47.04405745916844),
        LatLng(-22.833730670292926, -47.042568188004125),
        LatLng(-22.836012669833377, -47.04416036101791),
        LatLng(-22.83634805986867, -47.05268883988106),
    )

    private lateinit var handler: Handler
    private lateinit var timeUpdater: Runnable

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        handler = Handler()

        timeUpdater = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime: String = dateFormat.format(Date())

                binding.horarioText.text = currentTime

                handler.postDelayed(this, 1000)
            }
        }

        handler.post(timeUpdater)

        auth = Firebase.auth

        database = FirebaseDatabase.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmail = arguments?.getString("USER_EMAIL")

        val userName = arguments?.getString("USER_NAME")

        val userId = arguments?.getString("USER_ID")

        Log.e("USER_DADOS", "USER: $userId, EMAIL: $userEmail, NOME: $userName")

        binding.txtName.text = userName

        punchTheClock()

        val calendar = Calendar.getInstance()

        val calendarBg = resources.getDrawable(R.drawable.calendar_border_white)

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

        when (dayOfWeek) {
            Calendar.MONDAY -> R.id.calendars
            Calendar.TUESDAY -> R.id.calendars2
            Calendar.WEDNESDAY -> R.id.calendars3
            Calendar.THURSDAY -> R.id.calendars4
            Calendar.FRIDAY -> R.id.calendars5
            else -> null
        }?.let {
            view.findViewById<TextView>(it).apply {
                background = calendarBg
            }
        }

        for ((index, textViewId) in days.withIndex()) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                var dayOfMonth = currentDayOfMonth + index + 2
                if (dayOfMonth > maxDaysInCurrentMonth) {
                    dayOfMonth -= maxDaysInCurrentMonth
                    calendar.add(Calendar.MONTH, 1)
                } else if (dayOfMonth < 1) {
                    calendar.add(Calendar.MONTH, -1)
                    dayOfMonth += calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                view.findViewById<TextView>(textViewId).text = dayOfMonth.toString()
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                var dayOfMonth = currentDayOfMonth + index + 1
                if (dayOfMonth > maxDaysInCurrentMonth) {
                    dayOfMonth -= maxDaysInCurrentMonth
                    calendar.add(Calendar.MONTH, 1)
                } else if (dayOfMonth < 1) {
                    calendar.add(Calendar.MONTH, -1)
                    dayOfMonth += calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                view.findViewById<TextView>(textViewId).text = dayOfMonth.toString()
            } else {
                var dayOfMonth = currentDayOfMonth - (todayIndex - index)
                if (dayOfMonth > maxDaysInCurrentMonth) {
                    dayOfMonth -= maxDaysInCurrentMonth
                    calendar.add(Calendar.MONTH, 1)
                }
                view.findViewById<TextView>(textViewId).text = dayOfMonth.toString()
            }

        }


        when(calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                view.findViewById<TextView>(R.id.segunda_text).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                view.findViewById<TextView>(R.id.segunda_number).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            Calendar.TUESDAY -> {
                view.findViewById<TextView>(R.id.terca_text).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                view.findViewById<TextView>(R.id.terca_number).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            Calendar.WEDNESDAY -> {
                view.findViewById<TextView>(R.id.quarta_text).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                view.findViewById<TextView>(R.id.quarta_number).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            Calendar.THURSDAY -> {
                view.findViewById<TextView>(R.id.quinta_text).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                view.findViewById<TextView>(R.id.quinta_number).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

            }

            Calendar.FRIDAY -> {
                view.findViewById<TextView>(R.id.sexta_text).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                view.findViewById<TextView>(R.id.sexta_number).apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }

    }

    private fun punchTheClock() {

        val auth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()


        binding.registrarPonto.setOnClickListener { //task ->

            val currentTimeMillis = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedTime: String = dateFormat.format(currentTimeMillis)
            val userId = auth.currentUser?.uid

            val databaseReference = database.child("pontos").push().key

            val ponto = HashMap<String, Any>()
            ponto["userId"] = userId as String
            ponto["timestamp"] = formattedTime as String

            if (databaseReference != null){
                if ( userId != null) {
                    if (ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
                            .addOnSuccessListener { location: Location? ->
                                if (location != null) {
                                    currentLocation = location
                                    val isInsidePolygon = isLocationInsidePolygon(currentLocation)

                                    if (isInsidePolygon) {
                                        database.child("pontos").child(databaseReference).setValue(ponto).addOnSuccessListener {
                                            Toast.makeText(requireContext(), "Ponto registrado em: $formattedTime", Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener {exception ->
                                            Toast.makeText(requireContext(), "Falha ao registrar ponto.", Toast.LENGTH_SHORT).show()
                                            Log.e("Registro de Ponto", "Falha ao registrar ponto.", exception)
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Você não está dentro da área permitida para registar o ponto", Toast.LENGTH_LONG).show()
                                    }

                                } else {
                                    //binding.messageTextView.text = "Não foi possível obter a localização atual. 1"
                                    Log.d("GET_LOCATION", "Não foi possível obter a localização atual. 1")
                                    //Toast.makeText(requireContext(), "Não foi possível obter a localização atual. 1", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                //binding.messageTextView.text = "Não foi possível obter a localização atual. 2"
                                Log.d("GET_LOCATION", "Não foi possível obter a localização atual. 2")
                                //Toast.makeText(requireContext(), "Não foi possível obter a localização atual. 2", Toast.LENGTH_LONG).show()

                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao obter o UserID", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnSignOut.setOnClickListener {
            auth.signOut()

            val intent = Intent(requireContext(), MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            //onDestroy()
        }
    }

    private fun isLocationInsidePolygon(location: Location): Boolean {
        val point = LatLng(location.latitude, location.longitude)
        return PolyUtil.containsLocation(point, polygonPoints, true)
    }
}