package com.example.controledeponto

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import java.util.Calendar
import com.example.controledeponto.databinding.FragmentRelatorioBinding
import java.util.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.ExperimentalTime
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class CustomSpinnerAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.textSize = 14f
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.textSize = 14f
        return view
    }
}

data class Point(
    val timestamp: String? = null,
    val userId: String? = null,
    var date: Date? = null,
    var month: String? = null
)

class RelatorioFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PointAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRelatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRelatorioBinding.bind(view)

        val months = resources.getStringArray(R.array.months_array)
        val monthAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_spinner, months.toList())
        binding.monthSpinner.adapter = monthAdapter

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 10..currentYear + 10).map { it.toString() }
        val yearAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_spinner, years.toList())
        binding.yearSpinner.adapter = yearAdapter

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PointAdapter()
        recyclerView.adapter = adapter

        binding.button.setOnClickListener {
            searchReports(binding)
        }

        auth = Firebase.auth
        binding.btnSignOut.setOnClickListener{
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun searchReports(binding: FragmentRelatorioBinding) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            val selectedMonthIndex = binding.monthSpinner.selectedItemPosition + 1
            val selectedYear = binding.yearSpinner.selectedItem.toString()

            val databaseReference = FirebaseDatabase.getInstance()
            val userRef = databaseReference.getReference("pontos")
            val query: Query = userRef.orderByChild("userId").equalTo(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val points = mutableListOf<Point>()
                    for (pointSnapshot in dataSnapshot.children) {
                        val point = pointSnapshot.getValue(Point::class.java)
                        if (point != null) {

                            val formatString = "yyyy-MM-dd HH:mm:ss"

                            val dataRecebida = point.timestamp
                            val dataInicial = "$selectedYear-$selectedMonthIndex-01 00:00:00"
                            val dataFinal = "$selectedYear-$selectedMonthIndex-31 23:59:59"

                            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                            val dataFormatada: Date = formato.parse(dataRecebida)
                            val dataInicialFormatada: Date = formato.parse(dataInicial)
                            val dataFinalFormatada: Date = formato.parse(dataFinal)

                            point.date = dataFormatada
                            point.month = binding.monthSpinner.selectedItem.toString()

                            if (isInDateRange(dataFormatada, dataInicialFormatada, dataFinalFormatada)) {
                                //Log.d("Main","$dataFormatada")
                                points.add(point)
                                binding.recyclerView.visibility = View.VISIBLE

                            }
                        }else{
                            binding.noDataTextView.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        adapter.submitList(points)
                    }
                    if (points.isEmpty()) {
                        binding.noDataTextView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.noDataTextView.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("RelatorioFragment", "Erro ao buscar dados: ${databaseError.message}")
                }
            })
        } else {
            Log.d("RelatorioFragment","Usuário não autenticado")
        }
    }
    fun isInDateRange(date: Date, startDate: Date, endDate: Date): Boolean {
        return date.after(startDate) && date.before(endDate)
    }
}