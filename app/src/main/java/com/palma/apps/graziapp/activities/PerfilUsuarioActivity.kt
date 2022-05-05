package com.palma.apps.graziapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityUserProfileBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.User
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.GlideLoader
import java.io.IOException

class PerfilUsuarioActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedProfileImageFileUri: Uri? = null
    private var mUserProfileImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constantes.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constantes.EXTRA_USER_DETAILS)!!

            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)

            if (mUserDetails.profileCompleted == 0) {
                binding.toolbarUserProfileActivity.title = getString(R.string.titulocompletarperfil)

                binding.etFirstName.isEnabled = false
                binding.etLastName.isEnabled = false

            }else{
                setUpActionBar()
                binding.toolbarUserProfileActivity.title = getString(R.string.edtperfil)

                GlideLoader(this).loadUserProfile(mUserDetails.image,binding.ivUserPhoto)

                if (mUserDetails.mobile != 0L){
                    binding.etMobileNumber.setText(mUserDetails.mobile.toString())
                }

                if (mUserDetails.gender == Constantes.MALE){
                    binding.rbMale.isChecked = true
                }else{
                    binding.rbFemale.isChecked = true
                }

            }
        }
        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarUserProfileActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarUserProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constantes.PICK_IMAGE_REQUEST_CODE) {
            if (data!!.data != null) {
                try {
                    mSelectedProfileImageFileUri = data.data
                    GlideLoader(this).loadUserProfile(
                        mSelectedProfileImageFileUri!!,
                        binding.ivUserPhoto
                    )

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        getString(R.string.noimage),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constantes.READ_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constantes.showImageChooser(this)
        } else {
            Toast.makeText(
                this,
                getString(R.string.permisosdeapp),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_user_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Constantes.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constantes.READ_STORAGE_PERMISSION_CODE
                    )
                }

            }
            R.id.btn_submit -> {
                if (validateUserProfileData()) {

                    showProgressDialog(getString(R.string.txtEspere))

                    if (mSelectedProfileImageFileUri != null) {

                        FireStoreClass().uploadImageToCloudStorage(
                            this,
                            mSelectedProfileImageFileUri,
                            Constantes.USER_PROFILE_IMAGE
                        )
                    } else {
                        updateUserProfileDetails()
                    }
                }

            }
        }
    }

    private fun updateEditedUserProfileDetails(){

        val userHashMap = HashMap<String,Any>()



        if (binding.etMobileNumber.text.toString() != mUserDetails.mobile.toString() && binding.etMobileNumber.text.toString() != "0L"){
            userHashMap[Constantes.MOBILE] = binding.etMobileNumber.text.toString().toLong()
        }
        userHashMap[Constantes.GENDER] = if (binding.rbMale.isChecked) Constantes.MALE else Constantes.FEMALE


    }

    private fun updateUserProfileDetails() {

        val userHashMap: HashMap<String, Any> = HashMap()

        if (mUserDetails.firstName != binding.etFirstName.text.toString().trim{ it<=' '}){
            userHashMap[Constantes.FIRST_NAME] = binding.etFirstName.text.toString().trim{ it<= ' '}
        }
        if (mUserDetails.lastName != binding.etLastName.text.toString().trim{ it<= ' '}){
            userHashMap[Constantes.LAST_NAME] = binding.etLastName.text.toString().trim{ it<= ' '}
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constantes.MALE
        } else {
            Constantes.FEMALE
        }
        if (mUserProfileImageUrl.isNotEmpty()) {
            userHashMap[Constantes.IMAGE] = mUserProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constantes.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constantes.GENDER] = gender
        }
        userHashMap[Constantes.PROFILE_COMPLETED] = 1
        userHashMap[Constantes.GENDER] = gender

        FireStoreClass().updateUserProfileData(this, userHashMap)

    }

    private fun validateUserProfileData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.errornumber), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.perfilupdated), Toast.LENGTH_SHORT)
            .show()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageUrl = imageURL

        updateUserProfileDetails()
    }
}