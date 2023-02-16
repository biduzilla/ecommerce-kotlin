package com.toddy.ecommerce.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.MainActivityEmpresa
import com.toddy.ecommerce.activity.usuario.MainActivityUsuario
import com.toddy.ecommerce.databinding.ActivityLoginBinding
import com.toddy.ecommerce.helper.FireBaseHelper

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

    private fun recuperaUsuario(id:String){
        val reference:DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(id)
        reference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    startActivity(Intent(baseContext,MainActivityUsuario::class.java))
                    finish()
                }else{
                    startActivity(Intent(baseContext,MainActivityEmpresa::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun validaDados(){
        val email:String = binding.edtEmail.text.toString()
        val senha:String = binding.edtSenha.text.toString()

        when{
            email.isEmpty() -> {
                binding.edtEmail.requestFocus()
                binding.edtEmail.error = "Campo Obrigatório"
            }
            senha.isEmpty() -> {
                binding.edtSenha.requestFocus()
                binding.edtSenha.error = "Campo Obrigatório"
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE
                login(email,senha)
            }
        }
    }

    private fun login(email:String,senha:String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){
                recuperaUsuario(FirebaseAuth.getInstance().currentUser!!.uid)
                finish()
            }else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, FireBaseHelper.validaErros(it.exception!!.message!!),Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun configClicks() {
        binding.btnVoltar.ibVoltar.setOnClickListener { finish() }

        binding.btnRecuperarSenha.setOnClickListener {
            startActivity(Intent(this, RecuperaContaActivity::class.java))
        }
        binding.btnCadastro.setOnClickListener {
            val intent = Intent(this, CadastrarContaActivity::class.java)
            resultLauncher.launch(intent)
        }
        binding.btnLogin.setOnClickListener {
            validaDados()
        }
    }
}