package io.github.larissafzl.confeccao.ui.telas.pedidos

// Listas usadas no formulário; outra confecção pode ajustar aqui
object OpcoesPedido {

    data class Prazo(val rotulo: String, val dias: List<Int>)

    val PRAZOS = listOf(
        Prazo("30/45/60 dias", listOf(30, 45, 60)),
        Prazo("30/60/90 dias", listOf(30, 60, 90)),
        Prazo("30 dias", listOf(30)),
        Prazo("À vista", listOf(0))
    )

    val TECNICAS = listOf("Silk", "Transfer", "Bordado")

    val POSICOES = listOf("Frente", "Costas")

    fun prazoDe(dias: List<Int>): Prazo =
        PRAZOS.firstOrNull { it.dias == dias } ?: Prazo(dias.joinToString("/") + " dias", dias)
}
