package com.toddy.ecommerce.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Categoria(private val reference: DatabaseReference = FirebaseDatabase.getInstance().reference) {


    var id: String = ""
    var nome: String = ""
    var urlImagem: String = ""
    var todas: Boolean = false

    init {

        this.id = reference.push().key!!
    }

    fun salvar() {
        reference.child("categorias").child(this.id).setValue(this)
    }

    fun remove() {
        reference.child("categorias").child(this.id).removeValue()
    }
}