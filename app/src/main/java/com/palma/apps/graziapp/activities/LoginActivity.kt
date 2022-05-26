package com.palma.apps.graziapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityLoginBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.User
import com.palma.apps.graziapp.utils.Constantes

class LoginActivity : BaseActivity() , View.OnClickListener {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvRegister.setOnClickListener (this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)

    }

    private fun loginUser() {

        if (validateLoginDetails()) {

            showProgressDialog(getString(R.string.txtEspere))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this@LoginActivity)
                    
                    } else {
                        hideProgressDialog()
                       /* Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()*/
                        showErrorSnackBar("Por favor ingrese un correo.",true)
                    }
                }.addOnFailureListener { exception ->
                    hideProgressDialog()
//                    Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.tv_forgot_password->{
                    val intent = Intent(this@LoginActivity,OlvidoContraseniaActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login->{
                    loginUser()
                }
                R.id.tv_register->{
                    val intent = Intent(this@LoginActivity,RegistroActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails() : Boolean{
        return when{
            TextUtils.isEmpty(binding.etEmail.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar("Por favor ingrese un correo.",true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar("Por favor ingrese una contrseña.",true)
                false
            }
            else->{
                true
            }

        }
    }
    fun userLoggedInSuccess(user : User){
        hideProgressDialog()

        if(user.profileCompleted == 0){
            val intent = Intent(this@LoginActivity, PerfilUsuarioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(Constantes.EXTRA_USER_DETALLES,user)
            startActivity(intent)
        }else{
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish()
    }
    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()

    }

}