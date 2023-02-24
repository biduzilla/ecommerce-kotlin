package com.toddy.ecommerce.fragment.loja

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.database.*
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.LojaFormProdutoActivity
import com.toddy.ecommerce.adapter.LojaProdutoAdapter
import com.toddy.ecommerce.databinding.FragmentLojaProdutoBinding
import com.toddy.ecommerce.model.Produto
import java.util.EventListener

class LojaProdutoFragment : Fragment(), LojaProdutoAdapter.OnClick {

    private var _binding: FragmentLojaProdutoBinding? = null
    private val binding get() = _binding!!
    private var lojaProdutoAdapter: LojaProdutoAdapter? = null
    private var produtoList = mutableListOf<Produto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    binding.textInfo.text = ""
                } else {
                    binding.textInfo.text = "Nenhum produto cadastrado"
                }
                binding.progressBar.visibility = View.GONE
                produtoList.reverse()
                lojaProdutoAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onClickListener(produto: Produto) {
        Toast.makeText(requireContext(), produto.titulo, Toast.LENGTH_SHORT).show()
    }

}