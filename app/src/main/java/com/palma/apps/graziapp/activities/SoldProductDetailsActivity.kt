package com.palma.apps.graziapp.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivitySoldProductDetailsBinding
import com.palma.apps.graziapp.modelos.Vendidos
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySoldProductDetailsBinding
    private  var mVendidosDetails : Vendidos? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constantes.EXTRA_SOLD_PRODUCT_DETAILS)){
            mVendidosDetails = intent.getParcelableExtra(Constantes.EXTRA_SOLD_PRODUCT_DETAILS)
            setUpUI(mVendidosDetails!!)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSoldProductDetailsActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarSoldProductDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpUI(productDetails : Vendidos){

        binding.tvOrderDetailsId.text = productDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        val orderDateTime = formatter.format(calendar.time)

        binding.tvOrderDetailsDate.text = orderDateTime

        GlideLoader(this).loadProductPicture(productDetails.image,binding.ivProductItemImage)
        binding.tvProductItemName.text = productDetails.title
        binding.tvProductItemPrice.text = "S/.${productDetails.price}"
        binding.tvSoldProductQuantity.text = productDetails.sold_quantity


        binding.tvMyOrderDetailsAddressType.text = productDetails.address.type
        binding.tvMyOrderDetailsFullName.text = productDetails.address.name
        binding.tvMyOrderDetailsAddress.text = "${productDetails.address.address} , ${productDetails.address.addressnumber}"
        binding.tvMyOrderDetailsAdditionalNote.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()){
            binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvMyOrderDetailsOtherDetails.text = productDetails.address.otherDetails
        }else{
            binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvMyOrderDetailsMobileNumber.text = productDetails.address.mobileNumber

        binding.tvOrderDetailsSubTotal.text = productDetails.sub_total_amount
        binding.tvOrderDetailsShippingCharge.text = productDetails.shipping_charge
        binding.tvOrderDetailsTotalAmount.text = productDetails.total_amount
    }
}