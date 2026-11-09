package io.github.larissafzl.confeccao.ui.telas.pedidos

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.dados.Parcela
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.StatusPedido
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.BotaoSecundario
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.LinhaInfo
import io.github.larissafzl.confeccao.ui.componentes.MenuOpcoes
import io.github.larissafzl.confeccao.ui.componentes.Painel
import io.github.larissafzl.confeccao.util.Formatos
import java.io.File

@Composable
fun TelaDetalhePedido(
    aoVoltar: () -> Unit,
    aoDespachar: (String) -> Unit,
    viewModel: DetalhePedidoViewModel = viewModel()
) {
    val tela by viewModel.tela.collectAsStateWithLifecycle()
    var confirmarExclusao by remember { mutableStateOf(false) }
    val pedido = (tela as? EstadoDetalhePedido.Pronto)?.pedido
    val contexto = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        BarraSuperior(
            pedido?.let { "Pedido nº ${Formatos.numeroPedido(it.numero)}" } ?: "Pedido",
            aoVoltar = aoVoltar,
            acoes = {
                if (pedido != null) MenuOpcoes(listOf("Excluir pedido" to { confirmarExclusao = true }))
            }
        )

        when (val estado = tela) {
            EstadoDetalhePedido.Carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            EstadoDetalhePedido.NaoEncontrado -> EstadoVazio(
                "Pedido não encontrado",
                "Ele pode ter sido excluído em outro aparelho."
            )
            is EstadoDetalhePedido.Pronto -> ConteudoPedido(
                estado.pedido,
                gerandoPdf = viewModel.gerandoPdf,
                aoAlternarPaga = { viewModel.alternarPaga(estado.pedido, it) },
                aoGerarPdf = {
                    viewModel.gerarPdf(contexto, estado.pedido) { compartilharPdf(contexto, it, estado.pedido) }
                },
                aoDespachar = { aoDespachar(viewModel.pedidoId) }
            )
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir pedido?") },
            text = { Text("O pedido e as parcelas serão apagados. Não é possível desfazer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    viewModel.excluir(aoVoltar)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ConteudoPedido(
    pedido: Pedido,
    gerandoPdf: Boolean,
    aoAlternarPaga: (Int) -> Unit,
    aoGerarPdf: () -> Unit,
    aoDespachar: () -> Unit
) {
    val total = Calculos.totalPedido(pedido)

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Painel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pedido.clienteNome,
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                ChipStatusPedido(pedido.status)
            }
            Text(
                listOfNotNull(
                    "Criado em ${Formatos.data(pedido.criadoEm)}",
                    pedido.previsaoDespacho?.takeIf { pedido.despacho == null }?.let { "Despacho prev. ${Formatos.data(it)}" }
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Painel(titulo = "Produto") {
            LinhaInfo("Modelo", pedido.modeloNome)
            LinhaInfo("Quantidade", "${pedido.quantidade} capas")
            LinhaInfo("Preço unitário", Formatos.moeda(pedido.precoUnitarioCentavos))
            LinhaInfo("Total", Formatos.moeda(total), destaque = true)
        }

        if (pedido.gravacao != null || pedido.cor.isNotBlank()) {
            Painel(titulo = "Gravação") {
                pedido.gravacao?.let {
                    LinhaInfo("Texto", it.texto)
                    LinhaInfo("Técnica e posição", "${it.tecnica} · ${it.posicao.lowercase()}")
                }
                if (pedido.cor.isNotBlank()) LinhaInfo("Cor", pedido.cor)
            }
        }

        Painel(titulo = "Pagamento · ${OpcoesPedido.prazoDe(pedido.prazosDias).rotulo}") {
            if (pedido.parcelas.isEmpty()) {
                Text(
                    "As parcelas são geradas quando o pedido for despachado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                pedido.parcelas.forEach { LinhaParcela(it) { aoAlternarPaga(it.numero) } }
            }
        }

        pedido.despacho?.let { d ->
            Painel(titulo = "Despacho") {
                LinhaInfo("Data", Formatos.data(d.data))
                if (d.volumes > 0) LinhaInfo("Volumes", "${d.volumes}")
                if (d.pesoKg > 0) LinhaInfo("Peso total", "${Formatos.numero(d.pesoKg)} kg")
                if (d.medidasCaixa.isNotBlank()) LinhaInfo("Medidas da caixa", d.medidasCaixa)
                if (d.transportadora.isNotBlank()) LinhaInfo("Transportadora", d.transportadora)
                if (d.valorFreteCentavos > 0) LinhaInfo("Valor do frete", Formatos.moeda(d.valorFreteCentavos))
                if (d.notaFiscal.isNotBlank()) LinhaInfo("Nota fiscal", d.notaFiscal)
            }
        }

        Painel(titulo = "Entrega") {
            LinhaInfo("Frete", pedido.frete.rotulo)
            if (pedido.observacoes.isNotBlank()) LinhaInfo("Observações", pedido.observacoes)
        }

        BotaoPrimario(
            if (gerandoPdf) "Gerando PDF..." else "Gerar PDF do pedido",
            aoGerarPdf,
            Modifier.fillMaxWidth(),
            icone = R.drawable.ic_pdf,
            habilitado = !gerandoPdf
        )
        if (pedido.status == StatusPedido.EM_PRODUCAO) {
            BotaoSecundario(
                "Marcar como despachado",
                aoDespachar,
                Modifier.fillMaxWidth(),
                icone = R.drawable.ic_caminhao
            )
        }
    }
}

@Composable
private fun LinhaParcela(parcela: Parcela, aoAlternar: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = parcela.paga, onCheckedChange = { aoAlternar() })
        Column(Modifier.weight(1f)) {
            Text("${parcela.numero}ª · ${Formatos.data(parcela.vencimento)}", style = MaterialTheme.typography.bodyMedium)
            Text(
                if (parcela.paga) "Recebida" else "A receber",
                style = MaterialTheme.typography.labelMedium,
                color = if (parcela.paga) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(Formatos.moeda(parcela.valorCentavos), style = MaterialTheme.typography.bodyMedium)
    }
}

private fun compartilharPdf(contexto: Context, arquivo: File, pedido: Pedido) {
    val uri = FileProvider.getUriForFile(contexto, "${contexto.packageName}.arquivos", arquivo)
    val envio = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Pedido nº ${Formatos.numeroPedido(pedido.numero)}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    contexto.startActivity(Intent.createChooser(envio, "Enviar pedido"))
}
