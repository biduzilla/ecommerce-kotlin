package com.toddy.ecommerce.activity.loja

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLojaConfigBinding
import com.toddy.ecommerce.model.Loja
import java.io.File
import java.util.*

class LojaConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLojaConfigBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var caminhoImagem: String? = null
    private var loja: Loja? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLojaConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recuperaLoja()
        initComponents()
        configClicks()
        startResult()
    }

    private fun configClicks() {
        binding.cardLogo.setOnClickListener {
            verificaPermissaoGaleria()
        }

        binding.btnSalvar.setOnClickListener {
            if (loja != null) {
                validaDados()
            } else {
                Toast.makeText(
                    this,
                    "Ainda estamos recuperando as informações da loja, aguarde...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initComponents() {
        binding.edtPedidoMinino.locale = Locale("PT", "br")
        binding.edtFrete.locale = Locale("PT", "br")
    }

    private fun validaDados() {
        val nomeLoja: String = binding.edtLoja.text.toString().trim()
        val cnpj: String = binding.edtCNPJ.masked
        val pedidoMinino: Double = (binding.edtPedidoMinino.rawValue / 100).toDouble()
        val frete: Double = (binding.edtFrete.rawValue / 100).toDouble()

        when {
            caminhoImagem == null -> {
                ocultarTeclado()
                Toast.makeText(this, "Escolha uma logo para sua loja!", Toast.LENGTH_SHORT).show()
            }
            nomeLoja.isEmpty() -> {
                binding.edtLoja.requestFocus()
                binding.edtLoja.error = "Campo Obrigatório!"
            }

            cnpj.isEmpty() || cnpj.length != 18 -> {
                binding.edtCNPJ.requestFocus()
                binding.edtCNPJ.error = "CNPJ Inválido"
            }
            else -> {
                binding.btnSalvar.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE

                loja!!.nome = nomeLoja
                loja!!.CNPJ = cnpj
                loja!!.pedidoMinino = pedidoMinino
                loja!!.freteGratis = frete

                salvarImgFirebase()
            }
        }
    }

    private fun recuperaLoja() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("loja")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loja = snapshot.getValue(Loja::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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

    private fun ocultarTeclado() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtPedidoMinino.windowToken, 0)
    }

    private fun salvarImgFirebase() {
        val storage: StorageReference = FirebaseStorage.getInstance().reference
            .child("imagens")
            .child("loja")
            .child("${loja!!.id}.jpeg")

        val uploadTask: UploadTask = storage.putFile(Uri.parse(caminhoImagem))
        uploadTask.addOnSuccessListener {
            storage.downloadUrl.addOnCompleteListener { task ->

                loja!!.urlLogo = task.result.toString()
                loja!!.salvar()

                binding.btnSalvar.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE

            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Erro com upload da imagem, tente novamente mais tarde",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}