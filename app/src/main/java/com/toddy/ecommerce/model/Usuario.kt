package com.toddy.ecommerce.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase

class Usuario : java.io.Serializable {
    var id: String = ""
    var nome: String = ""
    var email: String = ""
    var senha: String = ""
        @Exclude
        get

    fun salvar() {
        FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(this.id)
            .setValue(this)


    }

}