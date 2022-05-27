package com.palma.apps.graziapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constantes {
    //cambiar cada que se levanta la app
    const val URL  ="https://twenty-tigers-drop-181-66-150-181.loca.lt/grazia/api/"
    /*Collections*/
    const val USUARIOS: String = "usuarios"
    const val PRODUCTOS : String = "prendas"
    const val PRENDAS_CARRITOS : String = "prendas_carritos"
    const val ORDERS : String = "orden_prenda"
    const val SOLD_PRODUCT :String = "prendas_vendidas"


    const val GRAZIA_PREFERENCES: String = "Grazia"
    const val LOGGED_IN_USERNAME: String = "logged_in_user_name"
    const val EXTRA_USER_DETALLES: String = "extra_user_details"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"

    const val MALE: String = "Mujer"
    const val FEMALE: String = "Hombre"

    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"

    const val GENDER: String = "gender"
    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"

    const val PRODUCT_IMAGE : String = "product_image"

    const val USER_ID : String = "user_id"

    const val EXTRA_PRODUCT_ID  : String = "extra_product_id"
    const val PRODUCT_ID : String = "product_id"

    const val EXTRA_PRODUCT_OWNER_ID : String = "extra_product_owner_id"
    const val DEFAULT_CARD_QUANTITY : String = "1"

    const val CART_QUANTITY : String = "cart_quantity"

    const val USER_PROFILE_IMAGE : String = "user_profile_image"
    const val PROFILE_COMPLETED : String = "profileCompleted"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1


    const val HOME : String = "Casa"
    const val OFFICE : String = "Oficina"
    const val OTHER : String = "Otros"

    const val DIRECCIONES : String = "direccion"

    const val EXTRA_SELECT_DIRECCION : String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE : Int = 121

    const val EXTRA_DIRECCION_SELECCIONADA : String = "extra_selected_address"

    const val STOCK_CANTIDAD : String = "stock_quantity"

    const val DETALLES_EXTRA_ORDEN : String = "extra_my_order_details"


    const val EXTRA_PRODUCTOS_VENDIDOS : String = "extra_sold_product_details"


    fun showImageChooser(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri : Uri?) : String?{

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))

    }
}