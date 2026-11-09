package io.github.larissafzl.confeccao.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.ui.telas.pedidos.OpcoesPedido
import io.github.larissafzl.confeccao.ui.theme.Borda
import io.github.larissafzl.confeccao.ui.theme.RoxoMarca
import io.github.larissafzl.confeccao.ui.theme.RoxoSuave
import io.github.larissafzl.confeccao.ui.theme.Texto
import io.github.larissafzl.confeccao.ui.theme.TextoSecundario
import io.github.larissafzl.confeccao.ui.theme.VerdeMarca
import io.github.larissafzl.confeccao.util.Formatos
import java.io.File
import java.time.LocalDate

// Folha A4 em pontos (1/72 de polegada)
private const val LARGURA = 595
private const val ALTURA = 842
private const val MARGEM = 40f

class GeradorPdfPedido(private val context: Context) {

    private val documento = PdfDocument()
    private lateinit var pagina: PdfDocument.Page
    private lateinit var canvas: Canvas
    private var numeroPagina = 0
    private var y = 0f

    private val texto = paint(10f, Texto.toArgb())
    private val secundario = paint(9f, TextoSecundario.toArgb())
    private val negrito = paint(10f, Texto.toArgb(), Typeface.BOLD)
    private val tituloSecao = paint(11f, RoxoMarca.toArgb(), Typeface.BOLD)
    private val branco = paint(10f, android.graphics.Color.WHITE)
    private val brancoTitulo = paint(20f, android.graphics.Color.WHITE, Typeface.BOLD)
    private val linha = Paint().apply { color = Borda.toArgb(); strokeWidth = 1f }

    fun gerar(pedido: Pedido, cliente: Cliente?): File {
        novaPagina()
        cabecalho(pedido)
        secaoCliente(pedido, cliente)
        secaoProduto(pedido)
        secaoGravacao(pedido)
        secaoPagamento(pedido)
        secaoEntrega(pedido)
        documento.finishPage(pagina)

        val pasta = File(context.cacheDir, "pedidos").apply { mkdirs() }
        val arquivo = File(pasta, "pedido-${Formatos.numeroPedido(pedido.numero)}.pdf")
        arquivo.outputStream().use { documento.writeTo(it) }
        documento.close()
        return arquivo
    }

    private fun cabecalho(pedido: Pedido) {
        val fundo = Paint().apply { color = RoxoMarca.toArgb() }
        val faixa = Paint().apply { color = VerdeMarca.toArgb() }
        canvas.drawRect(0f, 0f, LARGURA.toFloat(), 96f, fundo)
        canvas.drawRect(0f, 0f, 10f, 96f, faixa)

        canvas.drawText(context.getString(R.string.app_name), MARGEM, 46f, brancoTitulo)
        val dadosEmpresa = listOf(R.string.empresa_cnpj, R.string.empresa_endereco, R.string.empresa_telefone)
            .map { context.getString(it) }
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        canvas.drawText(dadosEmpresa.ifEmpty { context.getString(R.string.slogan) }, MARGEM, 68f, branco)

        val direita = LARGURA - MARGEM
        val numero = "Pedido nº ${Formatos.numeroPedido(pedido.numero)}"
        val pNumero = paint(13f, android.graphics.Color.WHITE, Typeface.BOLD)
        canvas.drawText(numero, direita - pNumero.measureText(numero), 46f, pNumero)
        val emissao = "Emitido em ${Formatos.data(LocalDate.now())}"
        canvas.drawText(emissao, direita - branco.measureText(emissao), 68f, branco)
        y = 130f
    }

    private fun secaoCliente(pedido: Pedido, cliente: Cliente?) {
        titulo("Cliente")
        linhaInfo("Nome", cliente?.nome ?: pedido.clienteNome)
        cliente?.let {
            if (it.cnpj.isNotBlank()) linhaInfo("CNPJ", it.cnpj)
            if (it.inscricaoEstadual.isNotBlank()) linhaInfo("Inscrição estadual", it.inscricaoEstadual)
            if (it.endereco.isNotBlank()) linhaInfo("Endereço", it.endereco)
            if (it.local.isNotBlank()) linhaInfo("Cidade", it.local)
            if (it.telefone.isNotBlank()) linhaInfo("Telefone", it.telefone)
            if (it.email.isNotBlank()) linhaInfo("E-mail", it.email)
        }
    }

    private fun secaoProduto(pedido: Pedido) {
        titulo("Produto")
        val colunas = floatArrayOf(MARGEM, 300f, 400f, 480f)
        val fundo = Paint().apply { color = RoxoSuave.toArgb() }
        espacoPara(40f)
        canvas.drawRect(MARGEM - 6, y - 12, LARGURA - MARGEM + 6, y + 6, fundo)
        listOf("Modelo", "Quantidade", "Preço unit.", "Total").forEachIndexed { i, rotulo ->
            canvas.drawText(rotulo, colunas[i], y, negrito)
        }
        y += 20f
        val total = Calculos.totalPedido(pedido)
        listOf(
            pedido.modeloNome,
            "${pedido.quantidade} capas",
            Formatos.moeda(pedido.precoUnitarioCentavos),
            Formatos.moeda(total)
        ).forEachIndexed { i, valor -> canvas.drawText(valor, colunas[i], y, texto) }
        y += 10f
        canvas.drawLine(MARGEM, y, LARGURA - MARGEM, y, linha)
        y += 18f
        val rotuloTotal = "Total do pedido: ${Formatos.moeda(total)}"
        val pTotal = paint(12f, RoxoMarca.toArgb(), Typeface.BOLD)
        canvas.drawText(rotuloTotal, LARGURA - MARGEM - pTotal.measureText(rotuloTotal), y, pTotal)
        y += 8f
    }

    private fun secaoGravacao(pedido: Pedido) {
        if (pedido.gravacao == null && pedido.cor.isBlank()) return
        titulo("Gravação")
        pedido.gravacao?.let {
            linhaInfo("Texto", it.texto)
            linhaInfo("Técnica e posição", "${it.tecnica} · ${it.posicao.lowercase()}")
        }
        if (pedido.cor.isNotBlank()) linhaInfo("Cor", pedido.cor)
    }

    private fun secaoPagamento(pedido: Pedido) {
        titulo("Pagamento")
        linhaInfo("Prazo", OpcoesPedido.prazoDe(pedido.prazosDias).rotulo + " a partir do despacho")
        pedido.parcelas.forEach {
            linhaInfo("${it.numero}ª parcela · ${Formatos.data(it.vencimento)}", Formatos.moeda(it.valorCentavos))
        }
    }

    private fun secaoEntrega(pedido: Pedido) {
        titulo("Entrega")
        linhaInfo("Frete", pedido.frete.rotulo)
        val d = pedido.despacho
        if (d != null) {
            linhaInfo("Despachado em", Formatos.data(d.data))
            if (d.transportadora.isNotBlank()) linhaInfo("Transportadora", d.transportadora)
            if (d.volumes > 0) linhaInfo("Volumes", "${d.volumes}")
            if (d.notaFiscal.isNotBlank()) linhaInfo("Nota fiscal", d.notaFiscal)
        } else {
            pedido.previsaoDespacho?.let { linhaInfo("Previsão de despacho", Formatos.data(it)) }
        }
        if (pedido.observacoes.isNotBlank()) linhaInfo("Observações", pedido.observacoes)
    }

    private fun titulo(nome: String) {
        espacoPara(50f)
        y += 14f
        canvas.drawText(nome.uppercase(), MARGEM, y, tituloSecao)
        y += 6f
        canvas.drawLine(MARGEM, y, LARGURA - MARGEM, y, linha)
        y += 16f
    }

    private fun linhaInfo(rotulo: String, valor: String) {
        espacoPara(16f)
        canvas.drawText(rotulo, MARGEM, y, secundario)
        canvas.drawText(valor, 200f, y, texto)
        y += 16f
    }

    private fun espacoPara(altura: Float) {
        if (y + altura > ALTURA - MARGEM) {
            documento.finishPage(pagina)
            novaPagina()
            y = MARGEM + 10f
        }
    }

    private fun novaPagina() {
        numeroPagina++
        pagina = documento.startPage(PdfDocument.PageInfo.Builder(LARGURA, ALTURA, numeroPagina).create())
        canvas = pagina.canvas
    }

    private fun paint(tamanho: Float, cor: Int, estilo: Int = Typeface.NORMAL) = Paint().apply {
        isAntiAlias = true
        textSize = tamanho
        color = cor
        typeface = Typeface.create(Typeface.DEFAULT, estilo)
    }
}
