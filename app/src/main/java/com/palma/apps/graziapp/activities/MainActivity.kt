package com.palma.apps.graziapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palma.apps.graziapp.databinding.ActivityMainBinding
import com.palma.apps.graziapp.utils.Constantes

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(Constantes.GRAZIA_PREFERENCES, Context.MODE_PRIVATE)
        val userName = sharedPref.getString(Constantes.LOGGED_IN_USERNAME,"Hola")

        binding.userName.text = userName.toString()
    }
}