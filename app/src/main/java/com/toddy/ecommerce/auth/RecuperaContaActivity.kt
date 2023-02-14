package com.toddy.ecommerce.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityRecuperaContaBinding

class RecuperaContaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperaContaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperaContaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
    }

    private fun configClicks() {
        binding.btnVoltar.ibVoltar.setOnClickListener { finish() }
    }
}