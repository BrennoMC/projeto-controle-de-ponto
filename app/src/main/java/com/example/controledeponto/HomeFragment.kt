package com.example.controledeponto

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
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
import android.os.Build
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

data class Horarios (
    val horaInicio: String? = null,
    val horaFim: String? = null,
    val dia: String? = null,
    val userId: String? = null
)

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

        val userEmail = auth.currentUser?.email

        val userName = auth.currentUser?.displayName

        val userId = auth.currentUser?.uid

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

        // Obtém a referência do nó de horários no banco de dados
        val databaseReference = FirebaseDatabase.getInstance()

        val horariosRef = databaseReference.getReference("horarios")

        val query: Query = horariosRef.orderByChild("userId").equalTo(userId)

        Log.d("HORARIOS", "$query")

        // Adiciona um ouvinte de valor para a referência do nó de horários
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpa todas as notificações agendadas
                //clearNotifications()

                Log.d("HORARIOS", "$snapshot")

                // Obtém o dia da semana atual
                val calendar = Calendar.getInstance()
                val diaDaSemanaAtual = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "domingo"
                    Calendar.MONDAY -> "seg"
                    Calendar.TUESDAY -> "ter"
                    Calendar.WEDNESDAY -> "qua"
                    Calendar.THURSDAY -> "qui"
                    Calendar.FRIDAY -> "sex"
                    Calendar.SATURDAY -> "sab"
                    else -> ""
                }

                Log.d("HORARIOS", "Dia da semana: $diaDaSemanaAtual")

                val timeNotification = mutableListOf<Horarios>()

                // Itera sobre os horários do usuário
                for (child in snapshot.children) {
                    val horario = child.getValue(Horarios::class.java)

                    /*if(horario?.dia == "seg") {
                        Log.d("HORARIOS", "Dia: ${horario?.dia}, Inicio: ${horario?.horaInicio}")
                    }*/

                    if(horario?.dia == diaDaSemanaAtual) {

                        timeNotification.add(horario)

                        horario?.horaInicio?.let { scheduleNotification(it) }
                    }

                }
                Log.d("HORARIOS", "Lista: $timeNotification")

            }

            override fun onCancelled(error: DatabaseError) {
                // Tratamento de erro
            }
        })

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

    // Função para agendar uma notificação para um horário específico
    private fun scheduleNotification(horario: String) {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val horarioDate = dateFormat.parse("${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)} $horario:00")

        Log.d("HORARIOS", "Notificação criada | ${horarioDate}")
        Log.d("HORARIOS", "Notificação criada | ${horarioDate.time}")

        horarioDate?.let {
            val intent = Intent(requireContext(), NotificationPublisher::class.java)
            // Passa a data completa do dia para o Intent como um extra
            intent.putExtra("calendar", horarioDate.time)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                horarioDate.time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }


    private fun isLocationInsidePolygon(location: Location): Boolean {
        val point = LatLng(location.latitude, location.longitude)
        return PolyUtil.containsLocation(point, polygonPoints, true)
    }
}

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Obtém a data completa do dia do extra do Intent
        val timeInMillis = intent.getLongExtra("calendar", 0)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val diaDaSemana = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "domingo"
            Calendar.MONDAY -> "segunda-feira"
            Calendar.TUESDAY -> "terça-feira"
            Calendar.WEDNESDAY -> "quarta-feira"
            Calendar.THURSDAY -> "quinta-feira"
            Calendar.FRIDAY -> "sexta-feira"
            Calendar.SATURDAY -> "sábado"
            else -> ""
        }

        Log.d("HORARIOS", "NOTIFICATION PUBLISHER")

        // Cria um Intent para abrir a MainActivity quando a notificação for clicada
        val intent = Intent(context, HomeFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // Cria e envia a notificação
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel-id"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Horário de entrada")
            .setContentText("É hora de entrar! Você tem uma aula as $ Dia da semana: $diaDaSemana")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = Random().nextInt(1000)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}