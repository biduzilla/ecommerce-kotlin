package com.toddy.ecommerce.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable

class Produto : Serializable {
    var id: String = ""
    var idLocal: Int = 0
        @Exclude
        get
    var titulo: String = ""
    var descricao: String = ""
    var valorAntigo: Double = 0.0
    var valorAtual: Double = 0.0
    var rascunho: Boolean = false
    var idsCategoria = mutableListOf<String>()
    var urlsImagens = mutableListOf<ImagemUpload>()

    init {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        this.id = reference.push().key!!
    }

    fun salvar(novoProduto: Boolean) {
        FirebaseDatabase.getInstance().reference
            .child("produtos")
            .child(this.id)
            .setValue(this)
    }

    fun remover() {
        FirebaseDatabase.getInstance()
            .reference.child("produtos")
            .child(this.id)
            .removeValue()

        urlsImagens.forEach {
            FirebaseStorage.getInstance().reference
                .child("imagens")
                .child("produtos")
                .child(this.id)
                .child("imagem${it.index}.jpeg").delete()
        }
    }
}