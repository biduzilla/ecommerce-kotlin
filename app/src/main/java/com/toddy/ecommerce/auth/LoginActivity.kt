package com.toddy.ecommerce.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startResult()
        configClicks()
    }

    private fun startResult() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val email: String = it.data!!.getStringExtra("email")!!
                binding.edtEmail.setText(email)
            }
        }
    }

    private fun configClicks() {
        binding.btnRecuperarSenha.setOnClickListener {
            startActivity(Intent(this, RecuperaContaActivity::class.java))
        }
        binding.btnCadastro.setOnClickListener {
            val intent = Intent(this, CadastrarContaActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}