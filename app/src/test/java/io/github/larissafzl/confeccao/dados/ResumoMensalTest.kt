package io.github.larissafzl.confeccao.dados

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class ResumoMensalTest {

    private val novembro = YearMonth.of(2026, 11)
    private val mod05 = ModeloCapa(id = "m5", nome = "Mod. 05", custoCentavos = 403, precoCentavos = 790)

    private val pedidoNovembro = Pedido(
        modeloId = "m5", quantidade = 200, precoUnitarioCentavos = 790,
        criadoEm = LocalDate.of(2026, 11, 5)
    )
    private val pedidoOutubro = Pedido(
        modeloId = "m5", quantidade = 100, precoUnitarioCentavos = 790,
        criadoEm = LocalDate.of(2026, 10, 20),
        parcelas = listOf(
            Parcela(numero = 1, valorCentavos = 26_333, paga = true),
            Parcela(numero = 2, valorCentavos = 26_333),
            Parcela(numero = 3, valorCentavos = 26_334)
        )
    )

    @Test
    fun `conta so os pedidos feitos no mes`() {
        val resumo = ResumoMensal.calcular(novembro, listOf(pedidoNovembro, pedidoOutubro), listOf(mod05), emptyList())

        assertEquals(200, resumo.capasVendidas)
        assertEquals(158_000L, resumo.faturamentoCentavos)
    }

    @Test
    fun `lucro estimado usa o custo do modelo`() {
        val resumo = ResumoMensal.calcular(novembro, listOf(pedidoNovembro), listOf(mod05), emptyList())

        // 200 capas x R$ 3,24
        assertEquals(64_800L, resumo.lucroEstimadoCentavos)
    }

    @Test
    fun `a receber soma as parcelas em aberto de qualquer mes`() {
        val resumo = ResumoMensal.calcular(novembro, listOf(pedidoNovembro, pedidoOutubro), listOf(mod05), emptyList())

        assertEquals(52_667L, resumo.aReceberCentavos)
        assertEquals(2, resumo.parcelasAReceber)
    }

    @Test
    fun `reciclagem soma kg e valores do mes`() {
        val reciclagens = listOf(
            Reciclagem(data = LocalDate.of(2026, 11, 2), quantidade = 42.0, valorCentavos = 8_820),
            Reciclagem(data = LocalDate.of(2026, 11, 5), quantidade = 3.0, unidade = "unidades", valorCentavos = 500),
            Reciclagem(data = LocalDate.of(2026, 10, 30), quantidade = 10.0, valorCentavos = 1_000)
        )
        val resumo = ResumoMensal.calcular(novembro, emptyList(), emptyList(), reciclagens)

        assertEquals(42.0, resumo.quilosReciclados, 0.001)
        assertEquals(9_320L, resumo.reciclagemCentavos)
    }
}
