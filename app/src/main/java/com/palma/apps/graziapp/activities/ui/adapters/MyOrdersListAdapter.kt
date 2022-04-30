package com.palma.apps.graziapp.activities.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.MyOrderDetailsActivity
import com.palma.apps.graziapp.databinding.ItemListLayoutBinding
import com.palma.apps.graziapp.modelos.Order
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader

open class MyOrdersListAdapter(private val context: Context,
                               private val list : ArrayList<Order> ) : RecyclerView.Adapter<MyOrdersListAdapter.ViewHolder>() {
    class ViewHolder (val binding : ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false)
        return ViewHolder(ItemListLayoutBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image,holder.binding.ivItemImage)

        holder.binding.tvItemName.text = model.title
        holder.binding.tvItemPrice.text = "S/.${model.total_amount}"

//        holder.binding.ibDeleteProduct.visibility = View.GONE

        holder.itemView.setOnClickListener{
            val intent = Intent(context,MyOrderDetailsActivity::class.java)
            intent.putExtra(Constantes.EXTRA_MY_ORDER_DETAILS,model)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}