package com.toddy.ecommerce.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityRecuperaContaBinding
import com.toddy.ecommerce.helper.FireBaseHelper

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

    private fun validaDados() {
        val email: String = binding.edtEmail.text.toString()

        if (email.isEmpty()) {
            binding.edtEmail.requestFocus()
            binding.edtEmail.error = "Campo Obrigatório"
        } else {
            binding.progressBar.visibility = View.VISIBLE
            enviarEmail(email)
        }
    }

    private fun enviarEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Email de recuperação enviado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    FireBaseHelper.validaErros(it.exception!!.message!!),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.progressBar.visibility = View.GONE
    }
}