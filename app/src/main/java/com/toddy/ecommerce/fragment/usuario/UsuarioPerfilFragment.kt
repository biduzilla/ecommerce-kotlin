package com.toddy.ecommerce.fragment.usuario

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddy.ecommerce.R
import com.toddy.ecommerce.auth.LoginActivity
import com.toddy.ecommerce.databinding.FragmentUsuarioFavoritosBinding
import com.toddy.ecommerce.databinding.FragmentUsuarioPerfilBinding

class UsuarioPerfilFragment : Fragment() {

    private var _binding: FragmentUsuarioPerfilBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioPerfilBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(activity,LoginActivity::class.java))
        }
    }
}