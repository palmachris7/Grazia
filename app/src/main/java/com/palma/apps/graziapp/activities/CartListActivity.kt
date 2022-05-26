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
import com.palma.apps.graziapp.databinding.ActivityCartListBinding

import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Carrito
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.modelos.User
import com.palma.apps.graziapp.utils.Constantes
import kotlin.properties.Delegates

class CartListActivity : BaseActivity() {
    private var totale: Double? = null
    private lateinit var binding : ActivityCartListBinding
    private lateinit var mPrendaList : ArrayList<Prenda>
    private lateinit var mCarrito : ArrayList<Carrito>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnCheckout.setOnClickListener{
            val intent = Intent(this@CartListActivity,AddressListActivity::class.java)
            intent.putExtra(Constantes.EXTRA_SELECT_DIRECCION,true)
            startActivity(intent)
           // insertOnDatbase()
        }
    }
    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarCartListActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarCartListActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()

        getProductList()
    }

    //Trae lista carrito
    private fun getCartItemList(){
        //Carga infinito
       //showProgressDialog(getString(R.string.txtEspere))
        FireStoreClass().getCartList(this)
    }

    //Actualizar
    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }

    //Lista del carrito
    @SuppressLint("SetTextI18n")
    fun successCartItemList(cartList : ArrayList<Carrito>){
        hideProgressDialog()

        for(product in mPrendaList){
            for (cartItem in cartList){
                //make sure that the product in the cart
                if (product.product_id == cartItem.product_id){
                    //we just assign the product quantity to the cart item quantity
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                     cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCarrito = cartList

        if (mCarrito.size >0){
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemsList.setHasFixedSize(true)
            val adapter = ItemsCarritoListAdapter(this, mCarrito,true)
            binding.rvCartItemsList.adapter = adapter

            var subTotal  = 0.0
            var price = 0
            for(item in mCarrito){
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {

                    price = when {
                        item.price.contains(",") -> {
                            val index = item.price.indexOf(",")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        item.price.contains(".") -> {
                            val index = item.price.indexOf(".")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        else -> {
                            item.price.toInt()
                        }
                    }
                    val quantity = item.cart_quantity.toDouble()
                    subTotal += (price * quantity)
                }
            }
            binding.tvSubTotal.text = "S/. ${subTotal}"
            //Change the logic accordingly
            binding.tvShippingCharge.text = "S/. ${10}"

            if (subTotal >0 ){
                binding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 10
                totale=total
                binding.tvTotalAmount.text = "S/.${total}"
            }else{
                binding.llCheckout.visibility = View.GONE
            }
        }else{
            binding.tvNoCartItemFound.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.GONE
            binding.rvCartItemsList.visibility = View.GONE
        }
    }

/*    //Guardar en mysql
    fun insertOnDatbase(){
        val  rooturl = Constantes.URL
        val url= rooturl+"insertarV.php"
        val queue= Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"Venta insertada exitosamente",Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("total_venta",totale.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }*/

    //Enviar Mensaje
    fun enviarmensaje(){



    }

    //Prenda traida de firebase
    fun successProductListFromFireStore(prendaList : ArrayList<Prenda>){

        mPrendaList = prendaList
        hideProgressDialog()
        //Traer la lista de productos
        getCartItemList()

    }


    //Trae Lista de Productos
    private fun getProductList(){
        FireStoreClass().getAllProductList(this)
                //A veces carga infinito
            showProgressDialog(getString(R.string.txtEspere))
    }
    //Elimina prenda de carrito
    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()
        getCartItemList()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }
}