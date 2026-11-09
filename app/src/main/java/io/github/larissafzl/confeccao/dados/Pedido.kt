package io.github.larissafzl.confeccao.dados

import java.time.LocalDate

enum class StatusPedido(val rotulo: String) {
    EM_PRODUCAO("Em produção"),
    DESPACHADO("Despachado")
}

enum class Frete(val rotulo: String) {
    CLIENTE("Por conta do cliente"),
    CONFECCAO("Por conta da confecção")
}

data class Gravacao(
    val texto: String = "",
    val tecnica: String = "",
    val posicao: String = ""
)

data class Despacho(
    val data: LocalDate = LocalDate.now(),
    val volumes: Int = 0,
    val pesoKg: Double = 0.0,
    val medidasCaixa: String = "",
    val transportadora: String = "",
    val valorFreteCentavos: Long = 0,
    val notaFiscal: String = ""
)

data class Parcela(
    val numero: Int = 1,
    val vencimento: LocalDate = LocalDate.now(),
    val valorCentavos: Long = 0,
    val paga: Boolean = false
)

data class Pedido(
    val id: String = "",
    val numero: Int = 0,
    val clienteId: String = "",
    val clienteNome: String = "",
    val modeloId: String = "",
    val modeloNome: String = "",
    val quantidade: Int = 0,
    val precoUnitarioCentavos: Long = 0,
    val gravacao: Gravacao? = null,
    val cor: String = "",
    val prazosDias: List<Int> = PRAZOS_PADRAO,
    val frete: Frete = Frete.CLIENTE,
    val previsaoDespacho: LocalDate? = null,
    val observacoes: String = "",
    val criadoEm: LocalDate = LocalDate.now(),
    val status: StatusPedido = StatusPedido.EM_PRODUCAO,
    val despacho: Despacho? = null,
    val parcelas: List<Parcela> = emptyList()
) {
    companion object {
        // Prazo de pagamento usado pela confecção, contado a partir do despacho
        val PRAZOS_PADRAO = listOf(30, 45, 60)
    }
}
