package com.toddy.ecommerce.helper

class FireBaseHelper {

    companion object {
        fun validaErros(erro: String): String {
            var mensagem = ""
            when {
                erro.contains("There is no user record corresponding to this identifier") -> mensagem =
                    "Nenhuma conta encontrada com este e-mail."
                erro.contains("The email address is badly formatted") -> mensagem =
                    "Insira um e-mail válido."
                erro.contains("The password is invalid or the user does not have a password") -> mensagem =
                    "Senha inválida, tente novamente."
                erro.contains("The email address is already in use by another account") -> mensagem =
                    "Este e-mail já está em uso."
                erro.contains("Password should be at least 6 characters") -> mensagem =
                    "Insira uma senha mais forte."
            }
            return mensagem
        }
    }
}