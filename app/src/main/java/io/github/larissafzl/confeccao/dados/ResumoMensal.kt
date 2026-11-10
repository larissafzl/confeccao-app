package io.github.larissafzl.confeccao.dados

import java.time.YearMonth

data class ResumoMensal(
    val capasVendidas: Int = 0,
    val faturamentoCentavos: Long = 0,
    val lucroEstimadoCentavos: Long = 0,
    val quilosReciclados: Double = 0.0,
    val reciclagemCentavos: Long = 0,
    val aReceberCentavos: Long = 0,
    val parcelasAReceber: Int = 0
) {
    companion object {

        // Pedidos contam no mês em que foram feitos; "a receber" soma todas as parcelas em aberto
        fun calcular(
            mes: YearMonth,
            pedidos: List<Pedido>,
            modelos: List<ModeloCapa>,
            reciclagens: List<Reciclagem>
        ): ResumoMensal {
            val custoPorModelo = modelos.associate { it.id to it.custoCentavos }
            val doMes = pedidos.filter { YearMonth.from(it.criadoEm) == mes }
            val recicladoMes = reciclagens.filter { YearMonth.from(it.data) == mes }
            val abertas = pedidos.flatMap { it.parcelas }.filter { !it.paga }

            return ResumoMensal(
                capasVendidas = doMes.sumOf { it.quantidade },
                faturamentoCentavos = doMes.sumOf { Calculos.totalPedido(it) },
                lucroEstimadoCentavos = doMes.sumOf { pedido ->
                    val custo = custoPorModelo[pedido.modeloId] ?: 0
                    pedido.quantidade * (Calculos.precoSemImposto(pedido.precoUnitarioCentavos) - custo)
                },
                quilosReciclados = recicladoMes.filter { it.unidade == "kg" }.sumOf { it.quantidade },
                reciclagemCentavos = recicladoMes.sumOf { it.valorCentavos },
                aReceberCentavos = abertas.sumOf { it.valorCentavos },
                parcelasAReceber = abertas.size
            )
        }
    }
}
