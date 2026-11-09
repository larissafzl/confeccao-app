package io.github.larissafzl.confeccao.dados

data class Cliente(
    val id: String = "",
    val nome: String = "",
    val cnpj: String = "",
    val inscricaoEstadual: String = "",
    val endereco: String = "",
    val cidade: String = "",
    val uf: String = "",
    val telefone: String = "",
    val email: String = ""
) {
    val local: String
        get() = listOf(cidade, uf).filter { it.isNotBlank() }.joinToString(" · ")
}
