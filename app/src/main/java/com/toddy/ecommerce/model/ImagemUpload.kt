package com.toddy.ecommerce.model

import java.io.Serializable

data class ImagemUpload(
    var index: Int = 0,
    var caminhoImagem: String? = ""
) : Serializable

