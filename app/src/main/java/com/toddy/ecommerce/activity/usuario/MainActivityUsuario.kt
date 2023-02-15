package com.toddy.ecommerce.activity.usuario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityMainUsuarioBinding

class MainActivityUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityMainUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment

        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)


    }
}