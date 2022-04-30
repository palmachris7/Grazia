package com.palma.apps.graziapp.modelos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Direccion (
    val user_id : String = "",
    val name : String = "",
    val mobileNumber : String = "",

    val address : String = "",
    val addressnumber : String = "",
    val additionalNote : String = "",

    val type : String = "",
    val otherDetails : String = "",
    var id : String = "",
        ) : Parcelable