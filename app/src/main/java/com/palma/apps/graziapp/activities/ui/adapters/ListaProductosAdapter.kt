package com.palma.apps.graziapp.activities.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.DetalleProductoActivity
import com.palma.apps.graziapp.activities.ui.fragments.ProductsFragment
import com.palma.apps.graziapp.databinding.ItemListLayoutBinding
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader

open class ListaProductosAdapter (
    private val context : Context,
    private val list : ArrayList<Prenda>,
    private val fragment : ProductsFragment
        ) : RecyclerView.Adapter<ListaProductosAdapter.ViewHolder>(){
    class ViewHolder(val binding : ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false)
        return ViewHolder(ItemListLayoutBinding.bind(view))

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image,holder.binding.ivItemImage)

        holder.binding.tvItemName.text = model.title
        holder.binding.tvItemPrice.text = "S/. ${model.price}"

        holder.binding.ibDeleteProduct.setOnClickListener{
            fragment.deleteProduct(model.product_id)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,DetalleProductoActivity::class.java)
            intent.putExtra(Constantes.EXTRA_PRODUCT_ID,model.product_id)
            intent.putExtra(Constantes.EXTRA_PRODUCT_OWNER_ID,model.user_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}