package com.palma.apps.graziapp.modelos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order (
    val user_id : String = "",
    val items : ArrayList<Carrito> = ArrayList(),
    val address: Direccion = Direccion(),
    val title : String = "",
    val image : String = "",
    val sub_total_amount : String = "",
    val shipping_charge : String = "",
    val total_amount : String = "",
    val order_dateTime : Long = 0L,
    var id : String = ""
        ) : Parcelable