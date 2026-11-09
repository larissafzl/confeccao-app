package io.github.larissafzl.confeccao.dados

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalculosTest {

    private val mod05 = ModeloCapa(nome = "Mod. 05", custoCentavos = 403, precoCentavos = 790)

    private val pedido = Pedido(
        modeloNome = "Mod. 05",
        quantidade = 200,
        precoUnitarioCentavos = 790
    )

    @Test
    fun `lucro por capa desconta o imposto e o custo`() {
        // 7,90 x 0,92 = 7,27 (arredondado) - 4,03 = 3,24
        assertEquals(324L, Calculos.lucroPorCapa(mod05))
    }

    @Test
    fun `preco sem imposto desconta 8 por cento`() {
        assertEquals(727L, Calculos.precoSemImposto(790))
    }

    @Test
    fun `lucro pode ser negativo quando o custo e alto`() {
        val modelo = ModeloCapa(custoCentavos = 800, precoCentavos = 790)
        assertTrue(Calculos.lucroPorCapa(modelo) < 0)
    }

    @Test
    fun `total do pedido e quantidade vezes preco`() {
        assertEquals(158_000L, Calculos.totalPedido(pedido))
    }

    @Test
    fun `parcelas vencem 30, 45 e 60 dias depois do despacho`() {
        val parcelas = Calculos.gerarParcelas(158_000L, LocalDate.of(2026, 12, 12), listOf(30, 45, 60))

        assertEquals(
            listOf(LocalDate.of(2027, 1, 11), LocalDate.of(2027, 1, 26), LocalDate.of(2027, 2, 10)),
            parcelas.map { it.vencimento }
        )
        assertEquals(listOf(1, 2, 3), parcelas.map { it.numero })
    }

    @Test
    fun `centavos que sobram vao para a ultima parcela`() {
        val parcelas = Calculos.gerarParcelas(158_000L, LocalDate.of(2026, 12, 12), listOf(30, 45, 60))

        assertEquals(listOf(52_666L, 52_666L, 52_668L), parcelas.map { it.valorCentavos })
        assertEquals(158_000L, parcelas.sumOf { it.valorCentavos })
    }

    @Test
    fun `sem prazos nao gera parcelas`() {
        assertTrue(Calculos.gerarParcelas(1_000L, LocalDate.of(2026, 1, 1), emptyList()).isEmpty())
    }

    @Test
    fun `despachar muda o status e gera as parcelas`() {
        val despacho = Despacho(data = LocalDate.of(2026, 12, 12), volumes = 4)
        val despachado = Calculos.despachar(pedido, despacho)

        assertEquals(StatusPedido.DESPACHADO, despachado.status)
        assertEquals(despacho, despachado.despacho)
        assertEquals(3, despachado.parcelas.size)
        assertEquals(158_000L, despachado.parcelas.sumOf { it.valorCentavos })
    }
}
