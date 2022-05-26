package com.palma.apps.graziapp.activities.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.AddEditAddressActivity
import com.palma.apps.graziapp.activities.CheckoutActivity
import com.palma.apps.graziapp.databinding.ItemAddressLayoutBinding
import com.palma.apps.graziapp.modelos.Direccion
import com.palma.apps.graziapp.utils.Constantes

class DireccionLAdapter (private val context: Context,
                         private val list : ArrayList<Direccion>, private val mSelectedAddress : Boolean) : RecyclerView.Adapter<DireccionLAdapter.ViewHolder>() {
    class ViewHolder(val binding : ItemAddressLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_address_layout,parent,false)
        return ViewHolder(ItemAddressLayoutBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.tvAddressFullName.text = model.name
        holder.binding.tvAddressMobileNumber.text = model.mobileNumber
        holder.binding.tvAddressType.text = model.type
        holder.binding.tvAddressDetails.text = "${model.address}, ${model.addressnumber}"

        if (mSelectedAddress){
            holder.itemView.setOnClickListener{
                val intent = Intent(context,CheckoutActivity::class.java)
                intent.putExtra(Constantes.EXTRA_DIRECCION_SELECCIONADA,model)
                context.startActivity(intent)
            }
        }
    }

    fun notifyEditItem(activity : Activity,position: Int){
        val intent = Intent(context,AddEditAddressActivity::class.java)
        intent.putExtra(Constantes.EXTRA_ADDRESS_DETAILS,list[position])
        activity.startActivityForResult(intent,Constantes.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}