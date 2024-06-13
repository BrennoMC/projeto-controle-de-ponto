package com.example.controledeponto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledeponto.databinding.FragmentCalendarAdicionarHorarioBinding
import com.example.controledeponto.databinding.FragmentCalendarBinding
import com.example.controledeponto.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val horariosRef = FirebaseDatabase.getInstance().reference.child("horarios")
        var auth = FirebaseAuth.getInstance()

        val id = auth.currentUser?.uid
        val query: Query = horariosRef.orderByChild("userId").equalTo(id)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.listaHorariosSeg.removeAllViews()
                binding.listaHorariosTer.removeAllViews()
                binding.listaHorariosQua.removeAllViews()
                binding.listaHorariosQui.removeAllViews()
                binding.listaHorariosSex.removeAllViews()

                for (horarioSnapshot in dataSnapshot.children) {

                    val userId = horarioSnapshot.child("userId").getValue(String::class.java)
                    if (userId == id) {
                        val horaInicio =
                            horarioSnapshot.child("horaInicio").getValue(String::class.java)
                        val horaFim = horarioSnapshot.child("horaFim").getValue(String::class.java)
                        val dia = horarioSnapshot.child("dia").getValue(String::class.java)

                        val horarioTextView = TextView(requireContext()).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            text = "$horaInicio - $horaFim"
                        }

                        when (dia) {
                            "seg", "segunda" -> binding.listaHorariosSeg.addView(horarioTextView)
                            "ter", "terça" -> binding.listaHorariosTer.addView(horarioTextView)
                            "qua", "quarta" -> binding.listaHorariosQua.addView(horarioTextView)
                            "qui", "quinta" -> binding.listaHorariosQui.addView(horarioTextView)
                            "sex", "sexta" -> binding.listaHorariosSex.addView(horarioTextView)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adicionarHorario()
        binding.btnExcluirSeg.setOnClickListener {
            deletarHorario("seg")
        }

        binding.btnExcluirTer.setOnClickListener {
            deletarHorario("ter")
        }

        binding.btnExcluirQua.setOnClickListener {
            deletarHorario("qua")
        }

        binding.btnExcluirQui.setOnClickListener {
            deletarHorario("qui")
        }

        binding.btnExcluirSex.setOnClickListener {
            deletarHorario("sex")
        }

        auth = Firebase.auth
        binding.btnSignOut.setOnClickListener{
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }


    private fun deletarHorario(dia: String) {
        val horariosRef = FirebaseDatabase.getInstance().reference.child("horarios")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Query para encontrar e excluir o horário especificado
        val query: Query = horariosRef.orderByChild("userId").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (horarioSnapshot in dataSnapshot.children) {
                    val diaHorario = horarioSnapshot.child("dia").getValue(String::class.java)

                    if (diaHorario == dia) {
                        // Remove o nó do banco de dados
                        horarioSnapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Horário excluído com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Falha ao excluir horário.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate o erro aqui
                Log.e("CalendarFragment", "Erro ao excluir horário: ${error.message}")
                Toast.makeText(
                    requireContext(),
                    "Erro ao excluir horário: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun adicionarHorario() {
        binding.btnAdicionarHorario.setOnClickListener {
            val dialog = CalendarAdicionarHorario()
            val fragmentManager = (activity as FragmentActivity).supportFragmentManager
            fragmentManager.let { dialog.show(it, CalendarAdicionarHorario().tag) }
        }
    }
}