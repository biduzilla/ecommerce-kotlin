package com.toddy.ecommerce.fragment.loja

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Picasso
import com.toddy.ecommerce.R
import com.toddy.ecommerce.adapter.CategoriaAdapter
import com.toddy.ecommerce.databinding.DialogDeleteBinding
import com.toddy.ecommerce.databinding.DialogFormCategoriaBinding
import com.toddy.ecommerce.databinding.FragmentLojaCategoriaBinding
import com.toddy.ecommerce.model.Categoria
import com.tsuryo.swipeablerv.SwipeLeftRightCallback


class LojaCategoriaFragment : Fragment(), CategoriaAdapter.OnClick {

    private lateinit var categoriaBinding: DialogFormCategoriaBinding
    private var _binding: FragmentLojaCategoriaBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertDialog: AlertDialog
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var categoriaList = mutableListOf<Categoria>()
    private var caminhoImagem: String? = null
    private var categoria: Categoria? = null
    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLojaCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recuperaCategorias()
        startResult()
        configClicks()
        configRv()
    }

    private fun configRv() {
        binding.rvCategorias.layoutManager = LinearLayoutManager(context)
        binding.rvCategorias.setHasFixedSize(true)
        adapter = CategoriaAdapter(categoriaList, this)
        binding.rvCategorias.adapter = adapter

        binding.rvCategorias.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {

            }

            override fun onSwipedRight(position: Int) {
                showDialogDelete(categoriaList[position])
            }
        })
    }

    private fun recuperaCategorias() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("categorias")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                        binding.textInfo.text = ""
                        categoriaList.clear()
                        snapshot.children.forEach {
                            val categoria: Categoria = it.getValue(Categoria::class.java)!!
                            categoriaList.add(categoria)
                        }
                    }
                    else -> {
                        binding.textInfo.text = "Nenhuma categoria cadastrada"
                    }
                }
                binding.progressBar.visibility = View.GONE
                categoriaList.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun configClicks() {
        binding.btnAddCategoria.setOnClickListener {
            categoria = null
            showDialog()
        }

//        binding.btnAddCategoria.setOnTouchListener(object : OnTouchListener{
//            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
//
//                when(p1!!.action){
//                    MotionEvent.ACTION_UP -> {
//                        binding.btnAddCategoria.animate().scaleX(1.0f).scaleY(1.0f).duration = 0
//                    }
//                    MotionEvent.ACTION_DOWN -> {
//                        binding.btnAddCategoria.animate().scaleX(0.9f).scaleY(0.9f).duration = 0
//                    }
//                }
//                return true
//            }
//        })
    }

    private fun verificaPermissaoGaleria() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                abrirGaleria()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "Permissão Negada", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.create().setPermissionListener(permissionListener)
            .setDeniedMessage("Se você não aceitar a permissão não poderá acessar a galeria do dispositivo, deseja aceitar a permissão?")
            .setDeniedTitle("Permissões")
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                    val imgSelecionada: Uri = it.data!!.data!!
                    val bitmap: Bitmap

                    caminhoImagem = imgSelecionada.toString()

                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            imgSelecionada
                        )
                    } else {
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                imgSelecionada
                            )
                        ImageDecoder.decodeBitmap(source)
                    }
                    categoriaBinding.imgCategoria.setImageBitmap(bitmap)
                }
            }
    }

    private fun showDialogDelete(categoria: Categoria) {
        val alert = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog2)

        val deleteBiding: DialogDeleteBinding =
            DialogDeleteBinding.inflate(LayoutInflater.from(context))

        deleteBiding.btnFechar.setOnClickListener {
            adapter.notifyDataSetChanged()
            alertDialog.dismiss()
        }

        deleteBiding.btnSim.setOnClickListener {
            categoriaList.remove(categoria)

            if (categoriaList.isEmpty()) {
                binding.textInfo.text = "Nenhuma categoria cadastrada"
            } else {
                binding.textInfo.text = ""
            }

            categoria.remove()

            FirebaseStorage.getInstance().reference
                .child("imagens")
                .child("categorias")
                .child("${categoria.id}.jpeg").delete()
            adapter.notifyDataSetChanged()
            alertDialog.dismiss()

        }

        alert.setView(deleteBiding.root)

        alertDialog = alert.create()
        alertDialog.show()
    }

    private fun showDialog() {
        val alert = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)

        categoriaBinding = DialogFormCategoriaBinding.inflate(LayoutInflater.from(context))

        if (categoria != null) {
            categoriaBinding.edtCategoria.setText(categoria!!.nome)
            Picasso.get().load(categoria!!.urlImagem).into(categoriaBinding.imgCategoria)
            categoriaBinding.cbTodos.isChecked = categoria!!.todas
        }

        categoriaBinding.btnFechar.setOnClickListener { alertDialog.dismiss() }

        categoriaBinding.btnSalvar.setOnClickListener {
            val nomeCategoria: String = categoriaBinding.edtCategoria.text.toString()

//            when {
//                nomeCategoria.isEmpty() -> categoriaBinding.edtCategoria.error = "Campo Obrigatório"
//                caminhoImagem == null && categoria == null -> {
//                    ocultarTeclado()
//                    Toast.makeText(context, "Selecione uma imagem", Toast.LENGTH_SHORT).show()
//                }
//                categoria != null && categoria!!.urlImagem != "" -> {
//                    categoriaBinding.progressBar.visibility = View.VISIBLE
//                    categoria!!.salvar()
//                }
//                else -> {
//                    ocultarTeclado()
//                    categoria = Categoria()
//                    categoriaBinding.progressBar.visibility = View.VISIBLE
//                    categoria!!.nome = nomeCategoria
//                    categoria!!.todas = categoriaBinding.cbTodos.isChecked
//                    salvarImgFirebase()
//                }
//            }

            if (nomeCategoria.isNotEmpty()) {

                if (categoria == null) categoria = Categoria()

                categoria!!.nome = nomeCategoria
                categoria!!.todas = categoriaBinding.cbTodos.isChecked

                ocultarTeclado()

                categoriaBinding.progressBar.visibility = View.VISIBLE

                if (caminhoImagem != null) { // Novo cadastro ou edição da imagem
                    salvarImgFirebase()

                } else if (categoria!!.urlImagem != null) { // Edição de nome ou checkBox
                    categoria!!.salvar()
                    alertDialog.dismiss()

                } else { // Não preencheu a imagem
                    categoriaBinding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Escolha uma imagem para a categoria.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                categoriaBinding.edtCategoria.error = "Informação obrigatória."
            }
        }

        categoriaBinding.imgCategoria.setOnClickListener {
            verificaPermissaoGaleria()
        }

        alert.setView(categoriaBinding.root)

        alertDialog = alert.create()
        alertDialog.show()
    }

    private fun salvarImgFirebase() {
        val reference: StorageReference = FirebaseStorage.getInstance().reference
            .child("imagens")
            .child("categorias")
            .child("${categoria!!.id}.jpeg")

        val uploadTask: UploadTask = reference.putFile(Uri.parse(caminhoImagem))

        uploadTask.addOnSuccessListener {
            reference.downloadUrl.addOnCompleteListener { task ->
                val urlImg: String = task.result.toString()
                categoria!!.urlImagem = urlImg
                categoria!!.salvar()
                alertDialog.dismiss()

                categoria = null
            }.addOnFailureListener {
                alertDialog.dismiss()
                Toast.makeText(context, "Error ao salvar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ocultarTeclado() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onClickListener(categoria: Categoria) {
        this.categoria = categoria

        showDialog()
    }
}