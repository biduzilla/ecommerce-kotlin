package com.toddy.ecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.toddy.ecommerce.R
import com.toddy.ecommerce.model.Categoria
import com.toddy.ecommerce.model.Produto

class LojaProdutoAdapter(
    private val produtoList: MutableList<Produto>,
    private val context: Context,
    private val OnClickListener: OnClick
) :
    RecyclerView.Adapter<LojaProdutoAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduto: ImageView
        val textNomeProduto: TextView
        val textValorProduto: TextView
        val textDescontoProduto: TextView

        init {
            imgProduto = view.findViewById(R.id.imgProduto)
            textNomeProduto = view.findViewById(R.id.textNomeProduto)
            textValorProduto = view.findViewById(R.id.textValorProduto)
            textDescontoProduto = view.findViewById(R.id.textDescontoProduto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val produto: Produto = produtoList[position]
        holder.textNomeProduto.text = produto.titulo
        produto.urlsImagens.forEach {
            if (it.index == 0) {
                Picasso.get().load(it.caminhoImagem).into(holder.imgProduto)
            }
        }
        holder.textValorProduto.text = produto.valorAtual.toString()
        holder.textDescontoProduto.text = "15% OFF"

        holder.itemView.setOnClickListener {
            OnClickListener.onClickListener(produto)
        }
    }

    override fun getItemCount(): Int {
        return produtoList.size
    }

    interface OnClick {
        fun onClickListener(produto: Produto)
    }
}