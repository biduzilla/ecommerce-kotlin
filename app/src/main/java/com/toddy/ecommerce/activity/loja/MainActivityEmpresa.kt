package com.toddy.ecommerce.activity.loja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityMainEmpresaBinding

class MainActivityEmpresa : AppCompatActivity() {

    private lateinit var binding: ActivityMainEmpresaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}