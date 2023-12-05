package com.example.appallauca

import java.io.Serializable
import java.util.Date
import com.google.firebase.database.Exclude

data class Alquiler(
    val fechaEntrega:String, val fechaDevolucion:String,
    val placa:String, val cliente:String, val empleado:String,
    val precio:String,
    @Exclude
    val id: String
): Serializable
