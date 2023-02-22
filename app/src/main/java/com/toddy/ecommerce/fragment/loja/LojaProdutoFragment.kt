package com.toddy.ecommerce.fragment.loja

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.LojaFormProdutoActivity
import com.toddy.ecommerce.databinding.FragmentLojaProdutoBinding

class LojaProdutoFragment : Fragment() {

    private var _binding:FragmentLojaProdutoBinding? = null
    private val binding get() = _binding!!

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

    }

    private fun configClicks() {
        binding.toolbar.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), LojaFormProdutoActivity::class.java))
        }
    }

}