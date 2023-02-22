package com.toddy.ecommerce.activity.loja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLojaFormProdutoBinding
import com.toddy.ecommerce.databinding.BottomSheetFormProdutoBinding

class LojaFormProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLojaFormProdutoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLojaFormProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
    }

    private fun configClicks() {

        binding.imgProduto0.setOnClickListener {
            showBottonSheet()
        }

        binding.imgProduto1.setOnClickListener {
            showBottonSheet()
        }

        binding.imgProduto2.setOnClickListener {
            showBottonSheet()
        }
    }

    private fun showBottonSheet() {
        val sheetBinding: BottomSheetFormProdutoBinding =
            BottomSheetFormProdutoBinding.inflate(layoutInflater)

        val bottonSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)

        bottonSheetDialog.setContentView(sheetBinding.root)
        bottonSheetDialog.show()

        sheetBinding.btnCamera.setOnClickListener {
            Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show()
            bottonSheetDialog.dismiss()
        }

        sheetBinding.btnGaleria.setOnClickListener {
            Toast.makeText(this, "galeria", Toast.LENGTH_SHORT).show()
            bottonSheetDialog.dismiss()
        }

        sheetBinding.btnCancelar.setOnClickListener {
            Toast.makeText(this, "close", Toast.LENGTH_SHORT).show()
            bottonSheetDialog.dismiss()
        }

    }
}