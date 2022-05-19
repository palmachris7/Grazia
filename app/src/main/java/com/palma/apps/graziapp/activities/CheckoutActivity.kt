package com.palma.apps.graziapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.ui.adapters.ItemsCarritoListAdapter
import com.palma.apps.graziapp.databinding.ActivityCheckoutBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Direccion
import com.palma.apps.graziapp.modelos.Carrito
import com.palma.apps.graziapp.modelos.Order
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var mPrendaList: ArrayList<Prenda>
    private lateinit var mCartItemsList: ArrayList<Carrito>
    private var mSelectedDireccionDetails: Direccion? = null
    private var mSubTotal : Double  = 0.0
    private var mTotalAmount : Double  = 0.0
    private lateinit var mOrderDetails : Order

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constantes.EXTRA_SELECTED_ADDRESS)) {
            mSelectedDireccionDetails = intent.getParcelableExtra(Constantes.EXTRA_SELECTED_ADDRESS)
        }
        if (mSelectedDireccionDetails != null) {
            binding.tvCheckoutAddress.text =
                "${mSelectedDireccionDetails!!.address}, ${mSelectedDireccionDetails!!.addressnumber}"
            binding.tvCheckoutAdditionalNote.text = mSelectedDireccionDetails!!.additionalNote
            binding.tvCheckoutAddressType.text = mSelectedDireccionDetails!!.type
            binding.tvCheckoutFullName.text = mSelectedDireccionDetails!!.name
            if (mSelectedDireccionDetails!!.otherDetails.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mSelectedDireccionDetails!!.otherDetails
            } else {
                binding.tvCheckoutOtherDetails.visibility = View.GONE
            }
            binding.tvMobileNumber.text = mSelectedDireccionDetails!!.mobileNumber
        }
        getProductList()

        binding.btnPlaceOrder.setOnClickListener{
            placeAnOrder()
        }
    }

    private fun getProductList() {
        showProgressDialog(getString(R.string.txtEspere))
        FireStoreClass().getAllProductList(this@CheckoutActivity)
    }

    fun successProductListFromFirestore(prendaList: ArrayList<Prenda>) {
        mPrendaList = prendaList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FireStoreClass().getCartList(this@CheckoutActivity)
    }
    private fun placeAnOrder(){
        showProgressDialog(getString(R.string.txtEspere))

        if (mSelectedDireccionDetails != null){
            mOrderDetails = Order(
                FireStoreClass().getCurrentUserId(),
                mCartItemsList,
                mSelectedDireccionDetails!!,
                "Orden: ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.00",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            FireStoreClass().placeOrder(this@CheckoutActivity,mOrderDetails)
        }
    }

    @SuppressLint("SetTextI18n")
    fun successCartItemsList(cartList: ArrayList<Carrito>) {
        hideProgressDialog()

        for (product in mPrendaList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }

            }
        }
        mCartItemsList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = ItemsCarritoListAdapter(this, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        for(item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0){
                val quantity = item.cart_quantity.toInt()
                val price = item.price.toInt()
                //calculating subtotal

                mSubTotal += (price * quantity)
            }
        }
        binding.tvCheckoutSubTotal.text = "S/.${mSubTotal}"
        //we can use our own logic here in shipping charges
        binding.tvCheckoutShippingCharge.text = "S/.10.0"

        if (mSubTotal > 0){
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0

            binding.tvCheckoutTotalAmount.text = "S/.${mTotalAmount}"
        }else{
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }


    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarCheckoutActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarCheckoutActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    fun orderPlaceSuccess(){
        FireStoreClass().updateAllDetails(this,mCartItemsList,mOrderDetails)

    }
    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "Tu orden se realizó correctamente.", Toast.LENGTH_SHORT).show()
     //   insertOnDatbase()
        val intent = Intent(this@CheckoutActivity,DashboardActivity::class.java)
        //to clear the stack of activities or layer of activities and open the dashboard activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }

/*    fun insertOnDatbase(){

        val  rooturl = Constantes.URL
        val url= rooturl+"insertarV.php"
        val queue= Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"Cliente insertado exitosamente",Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("total_venta", mTotalAmount.toString())

                return parametros
            }
        }
        queue.add(resultadoPost)
    }*/


}