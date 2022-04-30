package com.palma.apps.graziapp.activities

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityForgotPasswordBinding

class OlvidoContraseniaActivity : BaseActivity() {
    private lateinit var binding : ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnSubmit.setOnClickListener{
            val email : String = binding.etEmail.text.toString().trim{ it<= ' '}

            if (email.isNotEmpty()){
                showProgressDialog(getString(R.string.txtEspere))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{
                        task->
                        hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(
                                this@OlvidoContraseniaActivity,
                                getString(R.string.emailenv),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()

                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }else{
                showErrorSnackBar("Ingrese su Email", true)
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarForgotPasswordActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}