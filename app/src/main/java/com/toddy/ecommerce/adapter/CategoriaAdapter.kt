package com.toddy.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.toddy.ecommerce.R
import com.toddy.ecommerce.model.Categoria

class CategoriaAdapter(
    private val categoriaList: MutableList<Categoria>,
    private val OnClickListener: OnClick
) :
    RecyclerView.Adapter<CategoriaAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCategoria: ImageView
        var nomeCategoria: TextView

        init {
            imgCategoria = view.findViewById(R.id.imgCategoria)
            nomeCategoria = view.findViewById(R.id.nomeCategoria)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_horizontal, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val categoria: Categoria = categoriaList[position]

        holder.nomeCategoria.text = categoria.nome
        Picasso.get().load(categoria.urlImagem).into(holder.imgCategoria)

        holder.itemView.setOnClickListener {
            OnClickListener.onClickListener(categoria)
        }

    }

    override fun getItemCount(): Int {
        return categoriaList.size
    }

    interface OnClick {
        fun onClickListener(categoria: Categoria)
    }
}