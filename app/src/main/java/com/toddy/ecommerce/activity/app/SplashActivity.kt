package com.toddy.ecommerce.activity.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.toddy.ecommerce.activity.usuario.MainActivityUsuario
import com.toddy.ecommerce.R
import com.toddy.ecommerce.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.getMainLooper()).postDelayed(Runnable {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }, 3000)
    }
}