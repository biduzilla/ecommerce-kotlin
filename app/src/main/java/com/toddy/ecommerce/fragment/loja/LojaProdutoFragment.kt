package com.toddy.ecommerce.fragment.loja

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.LojaFormProdutoActivity
import com.toddy.ecommerce.adapter.LojaProdutoAdapter
import com.toddy.ecommerce.databinding.DialogLojaProdutoBinding
import com.toddy.ecommerce.databinding.FragmentLojaProdutoBinding
import com.toddy.ecommerce.model.Produto
import java.util.EventListener

class LojaProdutoFragment : Fragment(), LojaProdutoAdapter.OnClick {

    private var _binding: FragmentLojaProdutoBinding? = null
    private val binding get() = _binding!!
    private var lojaProdutoAdapter: LojaProdutoAdapter? = null
    private var produtoList = mutableListOf<Produto>()
    private var dialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLojaProdutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configClicks()
        configRv()

    }

    override fun onStart() {
        super.onStart()
        recuperaProdutos()
    }

    private fun configClicks() {
        binding.toolbar.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), LojaFormProdutoActivity::class.java))
        }
    }

    private fun configRv() {
        binding.rvProdutos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProdutos.setHasFixedSize(true)
        lojaProdutoAdapter = LojaProdutoAdapter(produtoList, requireContext(), this)
        binding.rvProdutos.adapter = lojaProdutoAdapter
    }

    private fun recuperaProdutos() {
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("produtos")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    produtoList.clear()
                    snapshot.children.forEach {
                        val produto: Produto = it.getValue(Produto::class.java)!!
                        produtoList.add(produto)
                    }
                    listEmpty()
                }
                binding.progressBar.visibility = View.GONE
                produtoList.reverse()
                lojaProdutoAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun onClickListener(produto: Produto) {
        showDialog(produto)
    }

    private fun listEmpty() {
        if (produtoList.isEmpty()) {
            binding.textInfo.text = "Nenhum produto cadastrado"
        } else {
            binding.textInfo.text = ""
        }
    }

    private fun showDialog(produto: Produto) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val dialogBinding: DialogLojaProdutoBinding =
            DialogLojaProdutoBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBinding.checkbox.isChecked = produto.rascunho

        produto.urlsImagens.forEach {
            if (it.index == 0) {
                Picasso.get().load(it.caminhoImagem).into(dialogBinding.imgProduto)
            }
        }
        dialogBinding.textNomeProduto.text = produto.titulo

        dialogBinding.btnFechar.setOnClickListener { dialog!!.dismiss() }

        dialogBinding.btnDeletar.setOnClickListener {
            produto.remover()
            dialog!!.dismiss()
            Toast.makeText(requireContext(), "Produto removido com sucesso", Toast.LENGTH_SHORT)
                .show()

            listEmpty()
        }


        dialogBinding.checkbox.setOnCheckedChangeListener { check, b ->
            produto.rascunho = check.isChecked
            produto.salvar(false)
        }

        builder.setView(dialogBinding.root)

        dialog = builder.create()
        dialog!!.show()
    }

}