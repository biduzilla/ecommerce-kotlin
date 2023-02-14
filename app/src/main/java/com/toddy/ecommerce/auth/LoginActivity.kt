package com.toddy.ecommerce.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
    }

    private fun configClicks() {
        binding.btnRecuperarSenha.setOnClickListener {
            startActivity(Intent(this, RecuperaContaActivity::class.java))
        }
        binding.btnCadastro.setOnClickListener {
            startActivity(Intent(this, CadastrarContaActivity::class.java))
        }
    }
}