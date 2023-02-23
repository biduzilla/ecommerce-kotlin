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
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.ActivityLojaFormProdutoBinding
import com.toddy.ecommerce.databinding.BottomSheetFormProdutoBinding
import com.toddy.ecommerce.model.Categoria
import com.toddy.ecommerce.model.ImagemUpload
import com.toddy.ecommerce.model.Produto
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LojaFormProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLojaFormProdutoBinding

    private var resultCode: Int = 0
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var imagemUploadList = mutableListOf<ImagemUpload>()
    private var categoriaList = mutableListOf<Categoria>()

    private var produto: Produto? = null
    private var novoProduto: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLojaFormProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configClicks()
        initComponents()
        recuperaCategoria()
        startResult()
    }

    private fun startResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {

                    val caminhoImagem: String

                    if (resultCode <= 2) {
                        val imagemSelecionada: Uri = it.data!!.data!!
                        caminhoImagem = imagemSelecionada.toString()

                        when (resultCode) {
                            0 -> {
                                binding.imgFake0.visibility = View.GONE
                                binding.imgProduto0.setImageBitmap(getBitmap(imagemSelecionada))
                            }
                            1 -> {
                                binding.imgFake1.visibility = View.GONE
                                binding.imgProduto1.setImageBitmap(getBitmap(imagemSelecionada))
                            }
                            2 -> {
                                binding.imgFake2.visibility = View.GONE
                                binding.imgProduto2.setImageBitmap(getBitmap(imagemSelecionada))
                            }
                            else -> Toast.makeText(
                                this,
                                "Error carregar imagem selecionada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        configUploads(caminhoImagem)

                    } else {
                        val file = File(currentPhotoPath)
                        caminhoImagem = file.toURI().toString()

                        when (resultCode) {
                            3 -> {
                                binding.imgFake0.visibility = View.GONE
                                binding.imgProduto0.setImageURI(Uri.fromFile(file))
                            }
                            4 -> {
                                binding.imgFake1.visibility = View.GONE
                                binding.imgProduto1.setImageURI(Uri.fromFile(file))
                            }
                            5 -> {
                                binding.imgFake2.visibility = View.GONE
                                binding.imgProduto2.setImageURI(Uri.fromFile(file))
                            }
                            else -> Toast.makeText(
                                this,
                                "Error carregar imagem selecionada",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        configUploads(caminhoImagem)
                    }

                }
            }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
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

    private fun configClicks() {

        binding.include.ibVoltar.ibVoltar.setOnClickListener { finish() }

        binding.imgProduto0.setOnClickListener {
            showBottonSheet(0)
        }

        binding.imgProduto1.setOnClickListener {
            showBottonSheet(1)
        }

        binding.imgProduto2.setOnClickListener {
            showBottonSheet(2)
        }

        binding.btnSalvar.setOnClickListener {
            validaDados()
        }
    }

    private fun recuperaCategoria() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("categorias")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    categoriaList.clear()
                    snapshot.children.forEach {
                        val categoria: Categoria = it.getValue(Categoria::class.java)!!
                        categoriaList.add(categoria)
                    }
                } else {
                    Toast.makeText(baseContext, "Nenhuma categoria cadastrada", Toast.LENGTH_SHORT)
                        .show()
                }
                Log.i("infoteste", "onDataChange: ${categoriaList.size}")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initComponents() {
        binding.edtValorAntigo.locale = Locale("PT", "br")
        binding.edtValorAtual.locale = Locale("PT", "br")
    }

    private fun validaDados() {
        val titulo: String = binding.edtTitulo.text.toString().trim()
        val descricao: String = binding.etdDescricao.text.toString().trim()
        val valorAntigo: Double = binding.edtValorAntigo.rawValue.toDouble()
        val valorAtual: Double = binding.edtValorAtual.rawValue.toDouble()

        when {
            titulo.isEmpty() -> {
                binding.edtTitulo.requestFocus()
                binding.edtTitulo.error = "Campo Obrigatório"
            }

            descricao.isEmpty() -> {
                binding.etdDescricao.requestFocus()
                binding.etdDescricao.error = "Campo Obrigatório"
            }
            valorAtual <= 0 -> {
                binding.edtValorAtual.requestFocus()
                binding.edtValorAtual.error = "Valor Inválido"
            }
            else -> {
                if (produto == null) produto = Produto()
                produto!!.titulo = titulo
                produto!!.descricao = descricao
                if (valorAntigo > 0) produto!!.valorAntigo = valorAntigo / 100
                produto!!.valorAtual = valorAtual / 100

                if (novoProduto) {
                    if (imagemUploadList.size == 3) {
                        salvarImgFireBase()
                    } else {
                        ocultarTeclado()
                        Toast.makeText(this, "Escolha 3 imagens para o produto", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    if (imagemUploadList.size > 0) {
                        salvarImgFireBase()
                    } else {
                        produto!!.salvar(false)
                    }
                }
            }
        }
    }

    private fun ocultarTeclado() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtTitulo.windowToken, 0)
    }

    private fun showBottonSheet(code: Int) {

        this.resultCode = code

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

        when (resultCode) {
            0 -> resultCode = 3
            1 -> resultCode = 4
            2 -> resultCode = 5
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.toddy.ecommerce.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    resultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)

    }

    private fun configUploads(caminhoImagem: String) {

        val request: Int = when (resultCode) {
            0, 3 -> 0
            1, 4 -> 1
            2, 5 -> 2
            else -> 3
        }

        val imagemUpload = ImagemUpload(request, caminhoImagem)
        if (imagemUploadList.isNotEmpty()) {
            var encontrou = false
            imagemUploadList.forEach {
                if (it.index == request) {
                    encontrou = true
                }
            }

            if (encontrou) {
                imagemUploadList[request] = imagemUpload
            } else {
                imagemUploadList.add(imagemUpload)
            }
        } else {
            imagemUploadList.add(imagemUpload)
        }
    }

    private fun salvarImgFireBase() {

        imagemUploadList.forEach {
            val storage: StorageReference = FirebaseStorage.getInstance().reference
                .child("imagens")
                .child("produtos")
                .child(produto!!.id)
                .child("imagem${it.index}.jpeg")

            val uploadTask: UploadTask = storage.putFile(Uri.parse(it.caminhoImagem))
            uploadTask.addOnSuccessListener { uploadTask ->
                storage.downloadUrl.addOnCompleteListener { task ->

                    it.caminhoImagem = task.result.toString()
                    produto!!.urlsImagens.add(it)
//                    if (novoProduto) {
//                        produto!!.urlsImagens.add(it)
//                    } else {
//                        produto!!.urlsImagens[index] = task.result.toString()
//                    }

                    if (imagemUploadList.size == it.index + 1) {
                        produto!!.salvar(novoProduto)
                    }
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