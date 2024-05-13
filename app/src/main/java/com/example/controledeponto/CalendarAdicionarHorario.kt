package com.example.controledeponto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.controledeponto.databinding.FragmentCalendarAdicionarHorarioBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Horario (
    val horaInicio: String,
    val horaFim: String,
    val dia: String,
    val userId: String,
)
class CalendarAdicionarHorario : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCalendarAdicionarHorarioBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adicionarHorario()
    }

    private fun adicionarHorario() {


        val databaseReference = database.child("horarios").push().key

        binding.btnSalvar.setOnClickListener {
            val horaInicio = binding.textEntrada.text.toString()
            val horaFim = binding.textSaida.text.toString()
            val dia = binding.textDia.text.toString()
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid
            val horario = Horario(horaInicio, horaFim, dia, userId.toString())

            if (databaseReference != null) {
                database.child("horarios").child(databaseReference).setValue(horario).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Horário salvo com sucesso", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Falha ao salvar horário.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarAdicionarHorarioBinding.inflate(inflater, container, false)
        return binding.root
    }
}