package com.toddy.ecommerce.fragment.loja

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.toddy.ecommerce.R
import com.toddy.ecommerce.databinding.DialogFormCategoriaBinding
import com.toddy.ecommerce.databinding.FragmentLojaCategoriaBinding


class LojaCategoriaFragment : Fragment() {

    private var _binding: FragmentLojaCategoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog

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

    private fun configClicks() {
        binding.btnAddCategoria.setOnClickListener {
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


    private fun showDialog() {
        val alert = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val categoriaBinding = DialogFormCategoriaBinding.inflate(LayoutInflater.from(context))

        categoriaBinding.btnFechar.setOnClickListener { alertDialog.dismiss() }

        categoriaBinding.btnSalvar.setOnClickListener { alertDialog.dismiss() }

        alert.setView(categoriaBinding.root)

        alertDialog = alert.create()
        alertDialog.show()
    }
}