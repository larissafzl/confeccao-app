package io.github.larissafzl.confeccao.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

object Formatos {

    private val brasil: Locale = Locale.forLanguageTag("pt-BR")
    private val numero = DecimalFormat("#,##0.00", DecimalFormatSymbols(brasil))
    private val decimal = DecimalFormat("#,##0.##", DecimalFormatSymbols(brasil))
    private val inteiro = DecimalFormat("#,##0", DecimalFormatSymbols(brasil))
    private val mes = DateTimeFormatter.ofPattern("MMMM 'de' uuuu", brasil)
    private val data = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

    fun moeda(centavos: Long): String {
        val valor = numero.format(BigDecimal(centavos).movePointLeft(2).abs())
        return if (centavos < 0) "-R$ $valor" else "R$ $valor"
    }

    // Sem centavos, para os cards do painel
    fun moedaCurta(centavos: Long): String {
        val valor = inteiro.format(BigDecimal(centavos).movePointLeft(2).abs().setScale(0, RoundingMode.HALF_UP))
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

    fun mesAno(valor: YearMonth): String = valor.format(mes).replaceFirstChar { it.titlecase(brasil) }

    fun lerData(texto: String): LocalDate? = try {
        LocalDate.parse(texto.trim(), data)
    } catch (e: DateTimeParseException) {
        null
    }

    fun numero(valor: Double): String = decimal.format(valor)

    fun numeroPedido(numero: Int): String = numero.toString().padStart(4, '0')

    fun normalizar(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase(brasil)

    fun iniciais(nome: String): String {
        val palavras = nome.split(" ").filter { it.length > 2 }.ifEmpty { nome.split(" ").filter { it.isNotBlank() } }
        return palavras.take(2).joinToString("") { it.first().uppercase() }
    }
}
