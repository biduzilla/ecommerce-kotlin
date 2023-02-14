package com.toddy.ecommerce.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.toddy.ecommerce.helper.FireBaseHelper
import com.toddy.ecommerce.model.Usuario
import com.toddy.ecommerce.databinding.ActivityCadastrarContaBinding
class CadastrarContaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastrarContaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastrarContaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
    }

    private fun configClicks(){
        binding.btnVoltar.ibVoltar.setOnClickListener {
            finish()
        }
    }

    fun validaDados(view: View){
        val nome:String = binding.edtNome.text.trim().toString()
        val email:String = binding.edtEmail.text.trim().toString()
        val senha:String = binding.edtSenha.text.trim().toString()
        val confirmaSenha:String = binding.edtConfirmeSenha.text.trim().toString()

        when{
            nome.isEmpty() -> {
                binding.edtNome.requestFocus()
                binding.edtNome.error = "Campo Obrigatório"
            }
            email.isEmpty() -> {
                binding.edtEmail.requestFocus()
                binding.edtEmail.error = "Campo Obrigatório"
            }
            senha.isEmpty() -> {
                binding.edtSenha.requestFocus()
                binding.edtSenha.error = "Campo Obrigatório"
            }
            confirmaSenha.isEmpty() -> {
                binding.edtConfirmeSenha.requestFocus()
                binding.edtConfirmeSenha.error = "Campo Obrigatório"
            }
            senha != confirmaSenha -> {
                binding.edtConfirmeSenha.requestFocus()
                binding.edtConfirmeSenha.error = "Senhas diferentes"
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE
                val user = Usuario()
                user.nome = nome
                user.email = email
                user.senha = senha

                criarConta(user)
            }
        }
    }

    private fun criarConta(user: Usuario){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.senha).addOnCompleteListener {
            if (it.isSuccessful){
                val id:String = it.result.user!!.uid
                user.id = id
                user.salvar()

                val intent = Intent()
                intent.putExtra("email", user.email)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this, FireBaseHelper.validaErros(it.exception!!.message!!), Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}