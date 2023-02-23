package com.toddy.ecommerce.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase

class Produto(val reference: DatabaseReference = FirebaseDatabase.getInstance().reference) {
    var id: String = ""
    var idLocal: Int = 0
        @Exclude
        get
    var titulo: String = ""
    var descricao: String = ""
    var precoAntigo: Double = 0.0
    var precoAtual: Double = 0.0
    var rascunho: Boolean = false
    var idsCategoria = mutableListOf<String>()
    var urlsImagens = mutableListOf<String>()

    init {
        this.id = reference.push().key!!
    }

    fun salvar(novoProduto:Boolean){}
}