package com.toddy.ecommerce.activity.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.toddy.ecommerce.activity.usuario.MainActivityUsuario
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.MainActivityEmpresa
import com.toddy.ecommerce.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.getMainLooper()).postDelayed(Runnable {

            verificaAcesso()
        }, 3000)
    }

    private fun verificaAcesso() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            recuperaAcesso()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun recuperaAcesso() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    startActivity(Intent(baseContext, MainActivityUsuario::class.java))
                } else {
                    startActivity(Intent(baseContext, MainActivityEmpresa::class.java))
                }
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}