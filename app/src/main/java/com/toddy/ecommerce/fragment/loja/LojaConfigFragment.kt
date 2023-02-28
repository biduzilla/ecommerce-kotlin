package com.toddy.ecommerce.fragment.loja

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddy.ecommerce.R
import com.toddy.ecommerce.activity.loja.LojaConfigActivity
import com.toddy.ecommerce.activity.loja.LojaRecebimentosActivity
import com.toddy.ecommerce.databinding.FragmentLojaConfigBinding

class LojaConfigFragment : Fragment() {

    private var _binding: FragmentLojaConfigBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLojaConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configClicks()
    }

    private fun configClicks() {
        binding.btnConfigLoja.setOnClickListener {
            startActivity(Intent(requireContext(), LojaConfigActivity::class.java))
        }

        binding.btnPagamentos.setOnClickListener {
            startActivity(Intent(requireContext(), LojaRecebimentosActivity::class.java))
        }


    }


}