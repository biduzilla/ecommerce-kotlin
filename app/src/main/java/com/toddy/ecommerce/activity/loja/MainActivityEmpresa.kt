package com.toddy.ecommerce.activity.loja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityMainEmpresaBinding

class MainActivityEmpresa : AppCompatActivity() {

    private lateinit var binding: ActivityMainEmpresaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)
    }


}