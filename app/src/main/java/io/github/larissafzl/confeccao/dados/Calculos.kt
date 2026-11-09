package io.github.larissafzl.confeccao.dados

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

object Calculos {

    val IMPOSTO: BigDecimal = BigDecimal("0.08")

    fun precoSemImposto(precoCentavos: Long): Long = BigDecimal(precoCentavos)
        .multiply(BigDecimal.ONE - IMPOSTO)
        .setScale(0, RoundingMode.HALF_UP)
        .toLong()

    fun lucroPorCapa(modelo: ModeloCapa): Long =
        precoSemImposto(modelo.precoCentavos) - modelo.custoCentavos

    fun totalPedido(pedido: Pedido): Long = pedido.quantidade * pedido.precoUnitarioCentavos

    // Divide o total em parcelas iguais; os centavos que sobram vão para a última
    fun gerarParcelas(totalCentavos: Long, dataDespacho: LocalDate, prazosDias: List<Int>): List<Parcela> {
        if (prazosDias.isEmpty()) return emptyList()
        val valorBase = totalCentavos / prazosDias.size
        val sobra = totalCentavos - valorBase * prazosDias.size
        return prazosDias.mapIndexed { i, prazo ->
            Parcela(
                numero = i + 1,
                vencimento = dataDespacho.plusDays(prazo.toLong()),
                valorCentavos = if (i == prazosDias.lastIndex) valorBase + sobra else valorBase
            )
        }
    }

    fun despachar(pedido: Pedido, despacho: Despacho): Pedido = pedido.copy(
        status = StatusPedido.DESPACHADO,
        despacho = despacho,
        parcelas = gerarParcelas(totalPedido(pedido), despacho.data, pedido.prazosDias)
    )
}
