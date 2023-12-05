package com.example.appallauca

import retrofit2.http.Body
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface FirebaseAPI {

    @POST("alquileres.json")
    fun addAlquiler(@Body alquiler: Alquiler): Call<Alquiler>

    @PUT("alquileres/{id}.json")
    fun updateAlquiler(@Path("id") id: String, @Body alquiler: Alquiler): Call<Alquiler>

    @DELETE("alquileres/{id}.json")
    fun deleteAlquiler(@Path("id") id:String): Call<Alquiler>

    @GET("alquileres.json")
    fun getAlquiler(): Call<List<Alquiler>>
}