package com.toddy.ecommerce.fragment.loja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.FragmentLojaCategoriaBinding

class LojaCategoriaFragment : Fragment() {

    private var _binding: FragmentLojaCategoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog:AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLojaCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configClicks()
    }

    private fun configClicks(){
        binding.btnAddCategoria.setOnClickListener { showDialog() }
    }

    private fun showDialog(){
        val alert = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val view = layoutInflater.inflate(R.layout.dialog_form_categoria, null)

        alert.setView(view)

        alertDialog = alert.create()
        alertDialog.show()
    }
}