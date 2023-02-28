package com.toddy.ecommerce.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase

class Loja:java.io.Serializable {

    var id:String = ""
    var nome:String = ""
    var urlLogo:String = ""
    var CNPJ:String = ""
    var pedidoMinino:Double = 0.0
    var freteGratis:Double = 0.0
    var email:String = ""
    @Exclude
        get
    var senha:String = ""
    var publicKey:String = ""
    var acessToken:String = ""
    var parcelas:Int = 0

    fun salvar() {
        FirebaseDatabase.getInstance().reference
            .child("loja")
            .setValue(this)
    }

}