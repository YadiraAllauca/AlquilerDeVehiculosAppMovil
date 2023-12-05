package com.example.appallauca

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appallauca.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = Firebase.database
    private lateinit var messagesListener: ValueEventListener
    private val lista:MutableList<Alquiler> = ArrayList()
    val myRef = database.getReference("alquileres")
    private val adapter: AlquilerAdapter by lazy {
        AlquilerAdapter()
    }
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.rvListado
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        lista.clear()
        actualizarRecycler(binding.rvListado)
    }

    private fun actualizarRecycler(recyclerView: RecyclerView) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://alquilerbase-a276e-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseAPI::class.java)
        val retrofit = retrofitBuilder.getAlquiler()
        messagesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lista.clear()
                dataSnapshot.children.forEach { child ->
                    val pasaje: Alquiler? =
                        Alquiler(
                            child.child("fechaEntrega").getValue().toString(),
                            child.child("fechaDevolucion").getValue().toString(),
                            child.child("placa").getValue().toString(),
                            child.child("cliente").getValue().toString(),
                            child.child("empleado").getValue().toString(),
                            child.child("precio").getValue().toString(),
                            child.key.toString()
                        )
                    pasaje?.let { lista.add(it) }
                }
                adapter.updateListListado(lista)
                recyclerView.adapter = adapter


            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)
        eventos()
    }

    fun eventos(){
        binding.btnAddA.setOnClickListener{ v ->
            val intent = Intent(this, RegistrarActivity::class.java)
            v.context.startActivity(intent)
        }
        adapter.setOnClickListenerDelete={
            deleteAlquiler(it)
        }
        adapter.setOnClickListenerEdit = {
            val bundle  =  Bundle().apply {
                putSerializable(Constants.KEY_ALQUILER,it)
            }
            val intent = Intent(this, RegistrarActivity::class.java).putExtras(bundle)
            startActivity(intent)
        }
    }
    fun deleteAlquiler(alquiler: Alquiler){
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://alquilerbase-a276e-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseAPI::class.java)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Se eliminará el registro")
        builder.setMessage("¿Desea continuar?")
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            Executors.newSingleThreadExecutor().execute {
                val retrofit = retrofitBuilder.deleteAlquiler(alquiler.id)
                retrofit.enqueue( object :Callback<Alquiler>{
                    override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {}
                    override fun onFailure(call: Call<Alquiler>, t: Throwable) {}
                })
                runOnUiThread {
                    Toast.makeText(this,"Registro eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        })
        builder.show()




    }
}