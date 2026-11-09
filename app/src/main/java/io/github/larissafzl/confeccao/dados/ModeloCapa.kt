package io.github.larissafzl.confeccao.dados

data class ModeloCapa(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val custoCentavos: Long = 0,
    val precoCentavos: Long = 0
)
