package io.github.larissafzl.confeccao.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class FormatosTest {

    @Test
    fun `formata moeda no padrao brasileiro`() {
        assertEquals("R$ 7,90", Formatos.moeda(790))
        assertEquals("R$ 1.580,00", Formatos.moeda(158_000))
        assertEquals("R$ 0,05", Formatos.moeda(5))
        assertEquals("-R$ 0,10", Formatos.moeda(-10))
    }

    @Test
    fun `le valores digitados pelo usuario`() {
        assertEquals(790L, Formatos.lerMoeda("7,90"))
        assertEquals(790L, Formatos.lerMoeda("7,9"))
        assertEquals(700L, Formatos.lerMoeda("7"))
        assertEquals(790L, Formatos.lerMoeda("R$ 7,90"))
        assertEquals(158_000L, Formatos.lerMoeda("1.580,00"))
    }

    @Test
    fun `valor invalido retorna nulo`() {
        assertNull(Formatos.lerMoeda(""))
        assertNull(Formatos.lerMoeda("abc"))
        assertNull(Formatos.lerMoeda("7,90,1"))
    }

    @Test
    fun `formata e le datas`() {
        assertEquals("12/12/2026", Formatos.data(LocalDate.of(2026, 12, 12)))
        assertEquals(LocalDate.of(2026, 1, 5), Formatos.lerData("05/01/2026"))
    }

    @Test
    fun `data inexistente retorna nulo`() {
        assertNull(Formatos.lerData("31/02/2026"))
        assertNull(Formatos.lerData("2026-12-12"))
    }

    @Test
    fun `numero decimal so mostra casas quando precisa`() {
        assertEquals("58", Formatos.numero(58.0))
        assertEquals("58,5", Formatos.numero(58.5))
    }

    @Test
    fun `numero do pedido tem quatro digitos`() {
        assertEquals("0128", Formatos.numeroPedido(128))
        assertEquals("12345", Formatos.numeroPedido(12345))
    }

    @Test
    fun `normaliza texto para busca sem acento`() {
        assertEquals("atelie bela forma", Formatos.normalizar("Ateliê Bela Forma"))
        assertEquals("sumare", Formatos.normalizar("SUMARÉ"))
    }

    @Test
    fun `iniciais ignoram palavras curtas`() {
        assertEquals("CT", Formatos.iniciais("Casa do Terno Ltda"))
        assertEquals("EN", Formatos.iniciais("Elegance Noivas"))
        assertEquals("J", Formatos.iniciais("Jo"))
        assertEquals("", Formatos.iniciais(""))
    }
}
