package com.palma.apps.graziapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityDetallesProductoBinding

import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Carrito
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader

class DetalleProductoActivity : BaseActivity() , View.OnClickListener {

    private var mProductID : String = ""
    private var mOwnerID : String = ""
    private lateinit var mPrendaDetails : Prenda
    private lateinit var binding : ActivityDetallesProductoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constantes.EXTRA_PRODUCT_ID) && intent.hasExtra(Constantes.EXTRA_PRODUCT_OWNER_ID)){
            mProductID = intent.getStringExtra(Constantes.EXTRA_PRODUCT_ID)!!
            mOwnerID = intent.getStringExtra(Constantes.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Prenda id ::",mProductID)
        }

        if (mOwnerID == FireStoreClass().getCurrentUserId()){
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        }else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        setUpActionBar()

        getProductDetails()

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarProductDetailsActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun getProductDetails(){

        showProgressDialog(getString(R.string.txtEspere))
        FireStoreClass().getProductDetails(this,mProductID)
    }
    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(prenda : Prenda){

        mPrendaDetails = prenda

//        hideProgressDialog()

        GlideLoader(this).loadProductPicture(prenda.image,binding.ivProductDetailImage)
        binding.tvProductDetailsTitle.text = prenda.title
        binding.tvProductDetailsPrice.text = "S/.${prenda.price}"
        binding.tvProductDetailsAvailableQuantity.text = prenda.stock_quantity
        binding.tvProductDetailsDescription.text = prenda.description

        if (prenda.stock_quantity.toInt() == 0){
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.text = getString(R.string.lbl_text_out_of_stock)
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(this,R.color.colorSnackBarError)
            )
        }else{
            //check if his own prenda or not , No need to check everytime
            //we check only for those products that not created by user
            if (FireStoreClass().getCurrentUserId() == prenda.user_id){
                hideProgressDialog()
            }else{
                FireStoreClass().checkIfItemsExistsInCart(this,mProductID)
            }
        }


    }

    private fun addToCart(){
        val addToCart = Carrito(
            FireStoreClass().getCurrentUserId(),
            mOwnerID,
            mProductID,
            mPrendaDetails.title,
            mPrendaDetails.price,
            mPrendaDetails.image,
            Constantes.DEFAULT_CARD_QUANTITY,
            mPrendaDetails.stock_quantity,
            )

        showProgressDialog(getString(R.string.txtEspere))
        FireStoreClass().addCartItems(this,addToCart)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_add_to_cart->{
                    addToCart()

                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@DetalleProductoActivity,CartListActivity::class.java))
                }
            }
        }
    }
    fun productExistsInCart(){
        hideProgressDialog()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this@DetalleProductoActivity, getString(R.string.success_message_item_added_in_cart), Toast.LENGTH_SHORT).show()


        binding.btnGoToCart.visibility = View.VISIBLE
        binding.btnAddToCart.visibility = View.GONE
    }
}