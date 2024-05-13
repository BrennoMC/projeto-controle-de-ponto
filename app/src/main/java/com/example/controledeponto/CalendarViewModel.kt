package com.example.controledeponto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel: ViewModel() {
    var horarioEntrada = MutableLiveData<String>()
    var horarioSaida = MutableLiveData<String>()
}