package com.palma.apps.graziapp.activities.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ItemDashboardLayoutBinding
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.GlideLoader

class DashboardItemsListAdapter (
    private val context: Context,
    private val list : ArrayList<Prenda>
        ) : RecyclerView.Adapter<DashboardItemsListAdapter.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    class ViewHolder(val binding : ItemDashboardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout,parent,false)
        return ViewHolder(ItemDashboardLayoutBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image,holder.binding.ivDashboardItemImage)
        holder.binding.tvDashboardItemTitle.text = model.title
        holder.binding.tvDashboardItemPrice.text = "S/. ${model.price}"

        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position,model)
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
       return list.size
    }
    interface OnClickListener{
        fun onClick(position: Int, prenda: Prenda)

    }
}