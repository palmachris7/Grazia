package com.palma.apps.graziapp.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityAddProductBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri : Uri? = null
    private var mProductImageURL : String = ""
    private lateinit var binding : ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpActionBar()
        binding.btnSubmit.setOnClickListener(this)
        binding.ivAddUpdateProduct.setOnClickListener(this)
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarAddProductActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddProductActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_update_product->{
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constantes.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constantes.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit->{
                    if (validateProductDetails()){
                        uploadProductImage()
                    }
                    //Inserta al mysql
                    clickBtnInsertar()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constantes.PICK_IMAGE_REQUEST_CODE){
                if (data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit_24))

                    mSelectedImageFileUri = data.data
                   GlideLoader(this).loadUserProfile(data.data!!,binding.ivProductImage)
                }
            }
        }else{
            Log.e("Cancelado ::", "Cancelado por el usuario")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constantes.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constantes.showImageChooser(this)
            }else{
                Toast.makeText(this, getString(R.string.permisosdeapp), Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun uploadProductImage(){
        showProgressDialog(getString(R.string.txtEspere))

        FireStoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constantes.PRODUCT_IMAGE)
    }
    private fun validateProductDetails() : Boolean{
        return when{

            mSelectedImageFileUri == null ->{
                showErrorSnackBar(getString(R.string.selecimagen),true)
                false
            }
            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.mensajedeerroprod),true)
                false
            }
            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.precioerror),true)
                false
            }
            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.descripcionerror),true)
                false
            }
            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.cantidaderror),true)
                false
            }
            else ->{
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL : String){

//        hideProgressDialog()

        mProductImageURL = imageURL
    // showErrorSnackBar("Imagen: $imageURL",false)

        //Upload product details to the storage

        uploadProductDetails()

    }
     fun productUploadSuccess(){
        hideProgressDialog()
         Toast.makeText(this, getString(R.string.succesproductoagregado), Toast.LENGTH_SHORT).show()

         finish()

    }

    private fun uploadProductDetails(){

        val shared = getSharedPreferences(Constantes.GRAZIA_PREFERENCES,Context.MODE_PRIVATE)
        val userName = shared.getString(Constantes.LOGGED_IN_USERNAME,"")


        val productDetails = Prenda(
            FireStoreClass().getCurrentUserId(),
            userName!!,
            binding.etProductTitle.text.toString().trim{ it <= ' '},
            binding.etProductPrice.text.toString().trim{ it<= ' '},
            binding.etProductDescription.text.toString().trim{it <= ' '},
            binding.etProductQuantity.text.toString().trim{ it <= ' '},
            mProductImageURL
            )

        FireStoreClass().uploadProductDetails(this,productDetails)

    }

    fun clickBtnInsertar(){

        val  rooturl = Constantes.URL
        val url= rooturl+"insertarP.php"
        val queue= Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"Producto insertado exitosamente",Toast.LENGTH_LONG).show()
            },Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("idcategoria","1")
                parametros.put("codigo","app_movil")
                parametros.put("nombre",binding.etProductTitle.text.toString())
                parametros.put("stock",binding.etProductQuantity.text.toString())
                parametros.put("descripcion",binding.etProductDescription.text.toString())
                parametros.put("imagen", "1652331319.png")
                return parametros
            }
        }
        queue.add(resultadoPost)
    }



    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }
}