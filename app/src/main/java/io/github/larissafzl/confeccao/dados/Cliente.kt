package io.github.larissafzl.confeccao.dados

data class Cliente(
    val id: String = "",
    val nome: String = "",
    val cnpj: String = "",
    val telefone: String = "",
    val email: String = "",
    val cidade: String = ""
)
