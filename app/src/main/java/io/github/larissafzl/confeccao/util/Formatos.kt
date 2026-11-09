package io.github.larissafzl.confeccao.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

object Formatos {

    private val brasil: Locale = Locale.forLanguageTag("pt-BR")
    private val numero = DecimalFormat("#,##0.00", DecimalFormatSymbols(brasil))
    private val data = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

    fun moeda(centavos: Long): String {
        val valor = numero.format(BigDecimal(centavos).movePointLeft(2).abs())
        return if (centavos < 0) "-R$ $valor" else "R$ $valor"
    }

    fun lerMoeda(texto: String): Long? {
        val limpo = texto.replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".")
        if (limpo.isEmpty()) return null
        return try {
            BigDecimal(limpo).movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact()
        } catch (e: NumberFormatException) {
            null
        } catch (e: ArithmeticException) {
            null
        }
    }

    fun data(valor: LocalDate): String = valor.format(data)

    fun lerData(texto: String): LocalDate? = try {
        LocalDate.parse(texto.trim(), data)
    } catch (e: DateTimeParseException) {
        null
    }

    fun numeroPedido(numero: Int): String = numero.toString().padStart(4, '0')
}
