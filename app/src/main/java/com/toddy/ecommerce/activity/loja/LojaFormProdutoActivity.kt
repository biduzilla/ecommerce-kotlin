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
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso
import com.toddy.ecommerce.R
import com.toddy.ecommerce.adapter.CategoriaDialogAdapter
import com.toddy.ecommerce.databinding.ActivityLojaFormProdutoBinding
import com.toddy.ecommerce.databinding.BottomSheetFormProdutoBinding
import com.toddy.ecommerce.databinding.DialogDeleteBinding
import com.toddy.ecommerce.databinding.DialogFormProdutoCategoriaBinding
import com.toddy.ecommerce.model.Categoria
import com.toddy.ecommerce.model.ImagemUpload
import com.toddy.ecommerce.model.Produto
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LojaFormProdutoActivity : AppCompatActivity(), CategoriaDialogAdapter.OnClick {

    private lateinit var binding: ActivityLojaFormProdutoBinding
    private lateinit var categoriaBinding: DialogFormProdutoCategoriaBinding
    private lateinit var dialog: AlertDialog

    private var idCategoriaSelecionada = mutableListOf<String>()
    private var categoriaList = mutableListOf<Categoria>()
    private var categoriaSelecionadaList = mutableListOf<String>()

    private var resultCode: Int = 0
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var imagemUploadList = mutableListOf<ImagemUpload>()

    private var produto: Produto? = null
    private var novoProduto: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLojaFormProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtras()
        configClicks()
        initComponents()
        recuperaCategoria()
        startResult()
    }

    private fun getExtras() {
        intent.extras?.let {
            val bundle: Bundle = it
            produto = bundle.getSerializable("produtoSelecionado") as Produto
            configProdutos()
        }
    }

    private fun configProdutos() {
        novoProduto = false

        produto!!.urlsImagens.forEach {
            when (it.index) {
                0 -> Picasso.get().load(it.caminhoImagem).into(binding.imgProduto0)
                1 -> Picasso.get().load(it.caminhoImagem).into(binding.imgProduto1)
                2 -> Picasso.get().load(it.caminhoImagem).into(binding.imgProduto2)
            }
        }

        binding.edtTitulo.setText(produto!!.titulo)
        binding.edtDescricao.setText(produto!!.descricao)
        binding.edtValorAntigo.setText((produto!!.valorAntigo * 10).toString())
        binding.edtValorAtual.setText((produto!!.valorAtual * 10).toString())

        binding.imgFake0.visibility = View.GONE
        binding.imgFake1.visibility = View.GONE
        binding.imgFake2.visibility = View.GONE

        idCategoriaSelecionada.addAll(produto!!.idsCategoria)

    }

    private fun configCategoriasEdit() {
        if (!novoProduto) {
            categoriaList.forEach {
                if (produto!!.idsCategoria.contains(it.id)) {
                    categoriaSelecionadaList.add(it.nome)
                }
            }
            categoriaSelecionadaList.reverse()
            categoriasSelecionadas()
        }
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

    private fun configRv() {
        categoriaBinding.rvCategorias.layoutManager = LinearLayoutManager(this)
        categoriaBinding.rvCategorias.setHasFixedSize(true)
        val adapter = CategoriaDialogAdapter(idCategoriaSelecionada, categoriaList, this)
        categoriaBinding.rvCategorias.adapter = adapter
    }

    private fun showDialogCategoria() {
        val alert = AlertDialog.Builder(this, R.style.CustomAlertDialog2)
        categoriaBinding =
            DialogFormProdutoCategoriaBinding.inflate(LayoutInflater.from(this))

        categoriaBinding.btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        categoriaBinding.btnSalvar.setOnClickListener {
            dialog.dismiss()
        }

        if (categoriaList.isEmpty()) {
            categoriaBinding.textInfo.text = "Nenhuma categoria cadastrada"
        } else {
            categoriaBinding.textInfo.text = ""
        }

        categoriaBinding.progressBar.visibility = View.GONE

        configRv()

        alert.setView(categoriaBinding.root)

        dialog = alert.create()
        dialog.show()
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

        binding.btnEscolherCategoria.setOnClickListener {
            showDialogCategoria()
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
                    configCategoriasEdit()
                } else {
                    Toast.makeText(baseContext, "Nenhuma categoria cadastrada", Toast.LENGTH_SHORT)
                        .show()
                }
                categoriaList.reverse()
                Log.i("infoteste", "onDataChange: ${categoriaList.size}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun initComponents() {
        binding.edtValorAntigo.locale = Locale("PT", "br")
        binding.edtValorAtual.locale = Locale("PT", "br")

        if (novoProduto) {
            binding.include.tvTitulo.text = "Novo Produto"
        } else {
            binding.include.tvTitulo.text = "Edição Produto"
        }
    }

    private fun validaDados() {
        val titulo: String = binding.edtTitulo.text.toString().trim()
        val descricao: String = binding.edtDescricao.text.toString().trim()
        val valorAntigo: Double = binding.edtValorAntigo.rawValue.toDouble()
        val valorAtual: Double = binding.edtValorAtual.rawValue.toDouble()

        when {
            titulo.isEmpty() -> {
                binding.edtTitulo.requestFocus()
                binding.edtTitulo.error = "Campo Obrigatório"
            }

            descricao.isEmpty() -> {
                binding.edtDescricao.requestFocus()
                binding.edtDescricao.error = "Campo Obrigatório"
            }
            valorAtual <= 0 -> {
                binding.edtValorAtual.requestFocus()
                binding.edtValorAtual.error = "Valor Inválido"
            }
            idCategoriaSelecionada.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Selecione pelo menos uma categoria para o produto",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(this, "Salvando", Toast.LENGTH_SHORT).show()
                if (produto == null) produto = Produto()

                produto!!.titulo = titulo
                produto!!.descricao = descricao
                if (valorAntigo > 0) produto!!.valorAntigo = valorAntigo / 100
                produto!!.valorAtual = valorAtual / 100
                produto!!.idsCategoria = idCategoriaSelecionada

                if (novoProduto) {
                    if (imagemUploadList.size == 3) {
                        imagemUploadList.forEachIndexed { index, imagemUpload ->
                            salvarImgFireBase(index, imagemUpload)
                        }

                    } else {
                        ocultarTeclado()
                        Toast.makeText(this, "Escolha 3 imagens para o produto", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    ocultarTeclado()
                    if (imagemUploadList.size > 0) {
                        imagemUploadList.forEachIndexed { index, imagemUpload ->
                            salvarImgFireBase(index, imagemUpload)
                        }
                    } else {
                        produto!!.salvar(false)
                    }
                }
                Toast.makeText(this, "Salvo", Toast.LENGTH_SHORT).show()
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

    private fun salvarImgFireBase(count: Int, img: ImagemUpload) {

        val storage: StorageReference = FirebaseStorage.getInstance().reference
            .child("imagens")
            .child("produtos")
            .child(produto!!.id)
            .child("imagem${img.index}.jpeg")

        val uploadTask: UploadTask = storage.putFile(Uri.parse(img.caminhoImagem))
        uploadTask.addOnSuccessListener {
            storage.downloadUrl.addOnCompleteListener { task ->

                img.caminhoImagem = task.result.toString()
                if (novoProduto) {
                    produto!!.urlsImagens.add(img)
                } else {
                    produto!!.urlsImagens[img.index] = img
                }

                if (imagemUploadList.size == count + 1) {
                    produto!!.salvar(novoProduto)
                    imagemUploadList.clear()

                    if (novoProduto) {
                        finish()
                    }
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

    override fun onClickListener(categoria: Categoria) {

        if (!idCategoriaSelecionada.contains(categoria.id)) {
            idCategoriaSelecionada.add(categoria.id)
            categoriaSelecionadaList.add(categoria.nome)
        } else {
            idCategoriaSelecionada.remove(categoria.id)
            categoriaSelecionadaList.remove(categoria.nome)
        }
        Log.i("infotext", categoriaSelecionadaList.size.toString())
        categoriasSelecionadas()
    }

    private fun categoriasSelecionadas() {
        val builder: StringBuilder = StringBuilder()
        categoriaSelecionadaList.forEach {
            if (it == categoriaSelecionadaList.last()) {
                builder.append(it)
            } else {
                builder.append(it).append(", ")

            }
        }
        if (categoriaSelecionadaList.isNotEmpty()) {
            binding.btnEscolherCategoria.text = builder
        } else {
            binding.btnEscolherCategoria.text = "Nenhuma Categoria Selecionada"
        }
    }
}