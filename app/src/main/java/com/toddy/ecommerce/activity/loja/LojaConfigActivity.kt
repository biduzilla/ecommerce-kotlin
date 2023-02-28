package com.toddy.ecommerce.activity.loja

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLojaConfigBinding
import java.io.File

class LojaConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLojaConfigBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var caminhoImagem: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLojaConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
        startResult()
    }

    private fun configClicks() {
        binding.cardLogo.setOnClickListener {
            verificaPermissaoGaleria()
        }
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

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun startResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {

                    val imagemSelecionada: Uri = it.data!!.data!!
                    caminhoImagem = imagemSelecionada.toString()
                    binding.imgLogo.setImageBitmap(getBitmap(imagemSelecionada))
                }
            }
    }

    private fun getBitmap(caminhoUri: Uri): Bitmap {

        val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                contentResolver,
                caminhoUri
            )
        } else {
            val source: ImageDecoder.Source =
                ImageDecoder.createSource(contentResolver, caminhoUri)
            ImageDecoder.decodeBitmap(source)
        }
        return bitmap
    }
}