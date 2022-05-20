package com.palma.apps.graziapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.databinding.ActivityRegistroBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.User
import com.palma.apps.graziapp.utils.Constantes

class RegistroActivity : BaseActivity() {
    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_back_24)
        binding.toolbarRegisterActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(getString(R.string.txtEspere))
            insertOnDatbase()
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val userId = firebaseUser.uid

                        val user = User(userId,
                            binding.etFirstName.text.toString().trim { it <= ' ' },
                            binding.etLastName.text.toString().trim { it <= ' ' },
                            binding.etEmail.text.toString().trim { it <= ' ' })

                        //Register the user to the FireStore firebase
                        FireStoreClass().registerUser(this, user)
                        //getting details and logged in the user
                        FireStoreClass().getUserDetails(this@RegistroActivity)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this@RegistroActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressDialog()
                    Toast.makeText(this@RegistroActivity, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Ingrese Nombre.", true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Ingrese Apelllido.", true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Ingrese email.", true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Ingrese contraseña.", true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Confirme password.", true)
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar("Contraseñas no coinciden", true)
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar("Acepte terminos y condiciones.", true)
                false
            }
            else -> {
                true
            }

        }
    }
    fun insertOnDatbase(){

        val  rooturl = Constantes.URL
        val url= rooturl+"insertarC.php"
        val queue= Volley.newRequestQueue(this)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> { response ->
                Toast.makeText(this,"Cliente insertado exitosamente",Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error ",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("nombre",binding.etFirstName.text.toString()+binding.etLastName.text.toString())
                parametros.put("email",binding.etEmail.text.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }


    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(this@RegistroActivity, "Registrado Correctamente", Toast.LENGTH_SHORT)
            .show()
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        val intent = Intent(this@RegistroActivity, PerfilUsuarioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constantes.EXTRA_USER_DETAILS, user)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()

    }

}