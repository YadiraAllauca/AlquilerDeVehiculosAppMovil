package com.example.appallauca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appallauca.databinding.ItemAlquilerBinding


class AlquilerAdapter(var listado:List<Alquiler> = emptyList()):RecyclerView.Adapter<AlquilerAdapter.AdapterViewHolder>(){

   lateinit var setOnClickListenerEdit:(Alquiler) -> Unit
    lateinit var setOnClickListenerDelete:(Alquiler) -> Unit

    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private var binding:ItemAlquilerBinding = ItemAlquilerBinding.bind(itemView)
        fun bind(alquiler: Alquiler){
            binding.txtentregaitem.text = alquiler.fechaEntrega
            binding.txtdevolucionitem.text = alquiler.fechaDevolucion
            binding.txtclienteitem.text = alquiler.cliente
            binding.txtempleadoitem.text = alquiler.empleado
            binding.txtplacaitem.text = alquiler.placa
            binding.txtprecioitem.text = "$"+alquiler.precio

            binding.btnEdit.setOnClickListener{
                setOnClickListenerEdit(alquiler)
            }
            binding.btnEliminar.setOnClickListener{
                setOnClickListenerDelete(alquiler)

            }
        }
    }
    fun updateListListado(pets:List<Alquiler>){
        this.listado = pets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alquiler, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val pet =listado[position]
        holder.bind(pet)
    }

    override fun getItemCount(): Int {
        return listado.size
    }

}