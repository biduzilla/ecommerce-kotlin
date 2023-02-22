package com.toddy.ecommerce.activity.loja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
            verificaPermissaoCamera()
            bottonSheetDialog.dismiss()
        }

        sheetBinding.btnGaleria.setOnClickListener {
            verificaPermissaoGaleria()
            bottonSheetDialog.dismiss()
        }

        sheetBinding.btnCancelar.setOnClickListener {
            bottonSheetDialog.dismiss()
        }
    }

    private fun verificaPermissaoCamera() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                abrirCamera()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(baseContext, "Permissão Negada", Toast.LENGTH_SHORT).show()
            }
        }
        showDialogPermissao(
            permissionListener,
            "Se você não aceitar a permissão não poderá acessar a camera do dispositivo, deseja aceitar a permissão?",
            listOf(android.Manifest.permission.CAMERA)
        )
    }

    private fun verificaPermissaoGaleria() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                abrirGaleria()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(baseContext, "Permissão Negada", Toast.LENGTH_SHORT).show()
            }
        }
        showDialogPermissao(
            permissionListener,
            "Se você não aceitar a permissão não poderá acessar a galeria do dispositivo, deseja aceitar a permissão?",
            listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

    private fun abrirCamera() {
    }

    private fun abrirGaleria() {
    }


    private fun showDialogPermissao(
        permissionListener: PermissionListener,
        msg: String,
        perm: List<String>
    ) {
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedTitle("Permissão negada")
            .setDeniedMessage(msg)
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(*perm.toTypedArray())
            .check()
    }
}