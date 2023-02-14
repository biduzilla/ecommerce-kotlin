package com.toddy.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.toddy.ecommerce.auth.LoginActivity
import com.toddy.ecommerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                Toast.makeText(this,"User já autentificado", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}