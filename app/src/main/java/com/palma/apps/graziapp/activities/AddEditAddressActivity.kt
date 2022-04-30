package com.palma.apps.graziapp.activities

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityAddEditAddressBinding

import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Direccion
import com.palma.apps.graziapp.utils.Constantes

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding : ActivityAddEditAddressBinding
    private var mDireccionDetails : Direccion? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constantes.EXTRA_ADDRESS_DETAILS)){
            mDireccionDetails = intent.getParcelableExtra(Constantes.EXTRA_ADDRESS_DETAILS)!!
        }
        if(mDireccionDetails != null){
            if (mDireccionDetails!!.id.isNotEmpty()){
                binding.tvTitle.text = getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = getString(R.string.btn_lbl_update)

                binding.etFullName.setText(mDireccionDetails?.name)
                binding.etPhoneNumber.setText(mDireccionDetails?.mobileNumber)
                binding.etZipCode.setText(mDireccionDetails?.addressnumber)
                binding.etAdditionalNote.setText(mDireccionDetails?.additionalNote)
                binding.etAddress.setText(mDireccionDetails?.address)

                when(mDireccionDetails?.type){
                    Constantes.HOME->{
                        binding.rbHome.isChecked = true
                    }
                    Constantes.OFFICE ->{
                        binding.rbOffice.isChecked = true
                    }else->{
                        binding.rbOther.isChecked = true
                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mDireccionDetails?.otherDetails)
                    }
                }
            }
        }

        binding.btnSubmitAddress.setOnClickListener{
            saveAddressToFireStore()
        }

        binding.rgType.setOnCheckedChangeListener{
            _,checkedId ->
            if (checkedId ==  R.id.rb_other){
                binding.tilOtherDetails.visibility = View.VISIBLE
            }else{
                binding.tilOtherDetails.visibility = View.GONE
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun validateData() : Boolean{

        return when{
            TextUtils.isEmpty(binding.etFullName.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Ingrese datos!!.",true)
                false
            }
            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Ingrese datos!!.",true)
                false
            }
            TextUtils.isEmpty(binding.etAddress.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Ingrese datos!!.",true)
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Ingrese datos!!.",true)
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim{ it <= ' '}
            ) -> {
                showErrorSnackBar("Ingrese datos!!.",true)
                false
            }
            else->{
                true
            }
        }

    }
     private fun saveAddressToFireStore(){
        val fullName : String = binding.etFullName.text.toString().trim{ it <= ' '}
        val phoneNumber : String = binding.etPhoneNumber.text.toString().trim{ it <= ' '}
        val address : String = binding.etAddress.text.toString().trim{ it <= ' '}
        val zipCode : String = binding.etZipCode.text.toString().trim{ it <= ' '}
        val additionalNote : String = binding.etAdditionalNote.text.toString().trim{ it <= ' '}
        val otherDetails : String = binding.etOtherDetails.text.toString().trim{ it <= ' '}

        if (validateData()){

            showProgressDialog(getString(R.string.txtEspere))

            val addressType : String = when{
                binding.rbHome.isChecked-> Constantes.HOME
                binding.rbOffice.isChecked-> Constantes.OFFICE
                else-> Constantes.OTHER
            }

            val addressModel = Direccion(
                FireStoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if(mDireccionDetails != null && mDireccionDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddressSuccess(this,addressModel,mDireccionDetails!!.id)
            }else {

                FireStoreClass().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess(){
        hideProgressDialog()
        if(mDireccionDetails != null && mDireccionDetails!!.id.isNotEmpty()){
            Toast.makeText(this, getString(R.string.msg_your_address_updated_successfully), Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, getString(R.string.address_added_success_msg), Toast.LENGTH_SHORT)
                .show()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
}