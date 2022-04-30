package com.palma.apps.graziapp.modelos

import android.os.Parcelable

/**
 * A data modelos class for Sold Prenda with required fields.
 */
@kotlinx.parcelize.Parcelize
data class Vendidos(
    val user_id: String = "",
    val title: String = "",
    val price: String = "",
    val sold_quantity: String = "",
    val image: String = "",
    val order_id: String = "",
    val order_date: Long = 0L,
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val address: Direccion = Direccion(),
    var id: String = "",
) : Parcelable
