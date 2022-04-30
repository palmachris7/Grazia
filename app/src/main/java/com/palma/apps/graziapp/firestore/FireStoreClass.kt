package com.palma.apps.graziapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.palma.apps.graziapp.activities.*
import com.palma.apps.graziapp.activities.ui.fragments.DashboardFragment
import com.palma.apps.graziapp.activities.ui.fragments.OrdersFragment
import com.palma.apps.graziapp.activities.ui.fragments.ProductsFragment
import com.palma.apps.graziapp.activities.ui.fragments.SoldProductsFragment
import com.palma.apps.graziapp.modelos.*
import com.palma.apps.graziapp.utils.Constantes

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegistroActivity, user: User) {
        mFireStore.collection(Constantes.USUARIOS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, exception.message.toString())
            }

    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }


    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constantes.USUARIOS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {

                    val sharedPref = activity.getSharedPreferences(
                        Constantes.GRAZIA_PREFER,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putString(
                        Constantes.USUARIO_LOGUEADO,
                        "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> activity.userLoggedInSuccess(user)
                        is RegistroActivity -> activity.userLoggedInSuccess(user)
                        is SettingsActivity -> activity.userDetailSuccess(user)
                    }
                }

            }.addOnFailureListener { exception ->
                when (activity) {
                    is LoginActivity -> activity.hideProgressDialog()
                    is RegistroActivity -> activity.hideProgressDialog()
                    is SettingsActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, exception.message.toString())
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Constantes.USUARIOS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is PerfilUsuarioActivity -> activity.userProfileUpdateSuccess()
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is PerfilUsuarioActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error al subir usuario", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val shrf: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType
                    + System.currentTimeMillis() + "."
                    + Constantes.getFileExtension(activity, imageFileUri)
        )

        shrf.putFile(imageFileUri!!)
            .addOnSuccessListener { snapShot ->
                Log.e("Firebase Image Url", snapShot.metadata!!.reference!!.downloadUrl.toString())
                snapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Image Url", uri.toString())

                        when (activity) {
                            is PerfilUsuarioActivity -> activity.imageUploadSuccess(uri.toString())
                            is AddProductActivity -> activity.imageUploadSuccess(uri.toString())
                        }
                    }
            }.addOnFailureListener { e ->
                when (activity) {
                    is PerfilUsuarioActivity -> activity.hideProgressDialog()
                    is AddProductActivity -> activity.hideProgressDialog()
                }
                Log.e("Error ", "Error al subir imagen a la db", e)
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, prendaDetails: Prenda) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .document()
            .set(prendaDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                Log.e("Error al agregar ::", e.message.toString())
            }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .whereEqualTo(Constantes.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i("Prenda List ::", document.documents.toString())
                val prendaList: ArrayList<Prenda> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Prenda::class.java)!!
                    product.product_id = i.id
                    prendaList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> fragment.successProductListFromFirestore(prendaList)
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ProductsFragment -> fragment.hideProgressDialog()
                }
                Log.e(fragment.javaClass.simpleName, "Error al listar", e)
            }
    }

    fun getProductDetails(activity: DetalleProductoActivity, productID: String) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .document(productID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val product = document.toObject(Prenda::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, e.message.toString())
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, e.message.toString())
                fragment.hideProgressDialog()
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(fragment.javaClass.simpleName, document.documents.toString())

                val prendaList: ArrayList<Prenda> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Prenda::class.java)!!
                    product.product_id = i.id

                    prendaList.add(product)
                }
                fragment.successDashboardItemList(prendaList)

            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e("Error :: ", e.message.toString())
            }
    }

    fun addCartItems(activity: DetalleProductoActivity, addToCart: Carrito) {
        mFireStore.collection(Constantes.PRENDAS_CARRITOS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, e.message.toString())
                activity.hideProgressDialog()
            }
    }

    fun checkIfItemsExistsInCart(activity: DetalleProductoActivity, productID: String) {
        mFireStore.collection(Constantes.PRENDAS_CARRITOS)
            .whereEqualTo(Constantes.USER_ID, getCurrentUserId())
            .whereEqualTo(Constantes.PRODUCT_ID, productID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, e.message.toString())
                activity.hideProgressDialog()

            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constantes.PRENDAS_CARRITOS)
            .whereEqualTo(Constantes.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val cartList: ArrayList<Carrito> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(Carrito::class.java)
                    if (cartItem != null) {
                        cartItem.id = i.id
                        cartList.add(cartItem)
                    }
                    when (activity) {
                        is CartListActivity -> activity.successCartItemList(cartList)
                        is CheckoutActivity -> activity.successCartItemsList(cartList)
                    }

                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> activity.hideProgressDialog()
                    is CheckoutActivity -> activity.hideProgressDialog()
                }
                Log.e("Error", "Error al listar", e)
            }
    }

    fun getAllProductList(activity: Activity) {
        mFireStore.collection(Constantes.PRODUCTOS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                val prendaList: ArrayList<Prenda> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Prenda::class.java)!!
                    product.product_id = i.id

                    prendaList.add(product)
                }
                when (activity) {
                    is CheckoutActivity -> activity.successProductListFromFirestore(prendaList)
                    is CartListActivity -> activity.successProductListFromFireStore(prendaList)
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> activity.hideProgressDialog()
                    is CartListActivity -> activity.hideProgressDialog()
                }
                Log.e("Error :: ", e.message.toString())
            }
    }

    fun removeItemFromCart(context: Context, card_id: String) {
        mFireStore.collection(Constantes.PRENDAS_CARRITOS)
            .document(card_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemoveSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e("Error", "Error al eliminar", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, hashMap: HashMap<String, Any>) {
        mFireStore.collection(Constantes.PRENDAS_CARRITOS)
            .document(cart_id)
            .update(hashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e("Error", "Error al actualizar", e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity, direccionInfo: Direccion) {
        mFireStore.collection(Constantes.ADDRESSES)
            .document()
            .set(direccionInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al agregar", e)
            }
    }

    fun getAddressList(activity: AddressListActivity) {
        mFireStore.collection(Constantes.ADDRESSES)
            .whereEqualTo(Constantes.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val direccionList: ArrayList<Direccion> = ArrayList()

                for (i in document.documents) {
                    val addressModel = i.toObject(Direccion::class.java)
                    if (addressModel != null) {
                        addressModel.id = i.id

                        direccionList.add(addressModel)
                    }
                }
                activity.setUpAddressInUI(direccionList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al listar", e)
            }
    }

    fun updateAddressSuccess(
        activity: AddEditAddressActivity,
        direccionInfo: Direccion,
        addressId: String
    ) {
        mFireStore.collection(Constantes.ADDRESSES)
            .document(addressId)
            .set(direccionInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al actualizar", e)

            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constantes.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al eliminar.", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, orderDetails: Order) {
        mFireStore.collection(Constantes.ORDERS)
            .document()
            .set(orderDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al agregar", e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Carrito>, order: Order) {

        val writeBatch = mFireStore.batch()
        for (cartItem in cartList) {

                   val productHashMap = HashMap<String,Any>()
                   //actualizar stock
                   productHashMap[Constantes.STOCK_QUANTITY] =
                       (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

                   //Productos
                   val documentReference = mFireStore.collection(Constantes.PRODUCTOS)
                       .document(cartItem.product_id)
                   //  hashMap
                   writeBatch.update(documentReference,productHashMap)

            val soldProduct = Vendidos(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_dateTime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address

            )
            val documentReferences = mFireStore.collection(Constantes.SOLD_PRODUCT)
                .document(cartItem.product_id)
            writeBatch.set(documentReferences,soldProduct)
        }

        for (cartItem in cartList) {

            //crear un document reference
            val documentReference = mFireStore.collection(Constantes.PRENDAS_CARRITOS)
                .document(cartItem.id)
            //this is the benefit of using batch we can do multiple things at a time.
            writeBatch.delete(documentReference)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error al actualizar.", e)
            }
    }

    //get the order list for order fragments
    fun getMyOrderList(fragment: OrdersFragment) {
        mFireStore.collection(Constantes.ORDERS)
            .whereEqualTo(Constantes.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()
                for (i in document.documents) {
                    val orderDetails = i.toObject(Order::class.java)
                    if (orderDetails != null) {
                        orderDetails.id = i.id
                        list.add(orderDetails)
                    }
                }
                fragment.populateOrdersListInUI(list)


            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e("Error", "Error al listar", e)
            }
    }
    fun getSoldProductsList(fragment : SoldProductsFragment){
        mFireStore.collection(Constantes.SOLD_PRODUCT)
            .whereEqualTo(Constantes.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                val list : ArrayList<Vendidos> = ArrayList()

                for (i in document.documents){
                    val soldProductItem = i.toObject(Vendidos::class.java)
                    if (soldProductItem != null){
                        soldProductItem.id = i.id
                        list.add(soldProductItem)
                    }
                }
                fragment.successSoldProductsList(list)

            }
            .addOnFailureListener {
                e->
                fragment.hideProgressDialog()
                Log.e("Error","Error al listar.",e)
            }
    }
}