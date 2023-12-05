package com.example.appallauca

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.appallauca.databinding.ActivityRegistrarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors

class RegistrarActivity : AppCompatActivity() {
    private lateinit var api: FirebaseAPI
    private lateinit var binding: ActivityRegistrarBinding
    var id = "0"
    var fecha1: EditText? = null
    var fecha2: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://alquilerbase-a276e-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
       api = retrofit.create(FirebaseAPI::class.java)
        eventos()
    }

    fun init(){
        var dayf:String
        var monthf: String
        fecha1 = binding.fecha1
        fecha1?.setOnClickListener {
            val Dialogfecha = DatePickerFragment{year, month, day ->
                dayf = "$day"
                monthf = "$month"
                if(dayf.length==1) dayf = "0"+"$day"
                if(monthf.length==1) monthf = "0"+"$month"
                fecha1?.setText(dayf+"/"+monthf+"/$year")}
            Dialogfecha.show(supportFragmentManager,"DataPicker")
        }
        fecha2 = binding.fecha2
        fecha2?.setOnClickListener {
            val Dialogfecha = DatePickerFragment{year, month, day ->
                dayf = "$day"
                monthf = "$month"
                if(dayf.length==1) dayf = "0"+"$day"
                if(monthf.length==1) monthf = "0"+"$month"
                fecha2?.setText(dayf+"/"+monthf+"/$year")}
            Dialogfecha.show(supportFragmentManager,"DataPicker")
        }
        val bundle = intent.extras
        bundle?.let {
            val alquiler = bundle.getSerializable(Constants.KEY_ALQUILER) as Alquiler
            id = alquiler.id
            binding.fecha1.setText(alquiler.fechaEntrega)
            binding.fecha2.setText(alquiler.fechaDevolucion)
            binding.placa.setText(alquiler.placa)
            binding.cliente.setText(alquiler.cliente)
            binding.empleado.setText(alquiler.empleado)
            binding.precio.setText(alquiler.precio)

        }

    }
    fun eventos(){
        binding.button.setOnClickListener {
            val fechaEntrega = binding.fecha1.text.toString()
            val fechaDevolucion= binding.fecha2.text.toString()
            val placa= binding.placa.text.toString()
            val cliente= binding.cliente.text.toString()
            val empleado= binding.empleado.text.toString()
            val precio= binding.precio.text.toString()
            if(fechaEntrega.isNotEmpty() and fechaDevolucion.isNotEmpty() and
                    placa.isNotEmpty() and cliente.isNotEmpty() and empleado.isNotEmpty()
            and precio.isNotEmpty()){
            if(id.equals("0")){
            agregar(Alquiler( fechaEntrega, fechaDevolucion, placa, cliente, empleado, precio,id))
                onBackPressed()
                }else{
                    actualizar(Alquiler( fechaEntrega, fechaDevolucion, placa, cliente, empleado, precio,id))
            }
                }else{
                Toast.makeText(applicationContext, "Completar todos los campos", Toast.LENGTH_LONG).show()
            }

        }

    }
    fun agregar(alquiler: Alquiler){
        Executors.newSingleThreadExecutor().execute {
            val call = api.addAlquiler(alquiler)
            call.enqueue(object : Callback<Alquiler> {
                override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {
                    if (response.isSuccessful) {
                        val addedProduct = response.body()
                    } else {
                        Toast.makeText(applicationContext, "error", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Alquiler>, t: Throwable) {
                }
            })

            runOnUiThread {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()

            }
        }
    }
    fun actualizar(alquiler: Alquiler){
        Executors.newSingleThreadExecutor().execute {
            val call = api.updateAlquiler(alquiler.id,alquiler)
            call.enqueue(object : Callback<Alquiler> {
                override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {
                    if (response.isSuccessful) {
                        val addedProduct = response.body()
                    } else {
                        Toast.makeText(applicationContext, "error", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Alquiler>, t: Throwable) {
                }
            })

            runOnUiThread {
                Toast.makeText(this, "Registro actualizado", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }

    class DatePickerFragment(val listener: (year:Int, month:Int, day:Int) -> Unit): DialogFragment(), DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(),this, year, month, day)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
            listener(year,month+1,day)
        }

    }


}