package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.dados.Frete
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.CampoData
import io.github.larissafzl.confeccao.ui.componentes.CampoSelecao
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto
import io.github.larissafzl.confeccao.ui.componentes.LinhaInfo
import io.github.larissafzl.confeccao.ui.componentes.Painel
import io.github.larissafzl.confeccao.ui.componentes.TituloSecao
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaNovoPedido(
    aoVoltar: () -> Unit,
    aoSalvar: (String) -> Unit,
    viewModel: NovoPedidoViewModel = viewModel()
) {
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    val modelos by viewModel.modelos.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior("Novo pedido", aoVoltar = aoVoltar)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TituloSecao("Cliente e produto")
            CampoSelecao(
                "Cliente", clientes, clientes.find { it.id == viewModel.clienteId },
                { viewModel.clienteId = it.id }, Modifier.fillMaxWidth(),
                dica = if (clientes.isEmpty()) "Cadastre um cliente primeiro" else "Selecione",
                textoDaOpcao = { it.nome }, erro = viewModel.erroCliente
            )
            CampoSelecao(
                "Modelo", modelos, modelos.find { it.id == viewModel.modeloId },
                viewModel::escolherModelo, Modifier.fillMaxWidth(),
                dica = if (modelos.isEmpty()) "Cadastre um modelo primeiro" else "Selecione",
                textoDaOpcao = { listOf(it.nome, it.descricao).filter(String::isNotBlank).joinToString(" · ") },
                erro = viewModel.erroModelo
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    "Quantidade", viewModel.quantidadeTexto, { viewModel.quantidadeTexto = it },
                    Modifier.weight(1f), tipoTeclado = KeyboardType.Number, erro = viewModel.erroQuantidade
                )
                CampoTexto(
                    "Preço unitário", viewModel.precoTexto, { viewModel.precoTexto = it },
                    Modifier.weight(1f), dica = "0,00", tipoTeclado = KeyboardType.Decimal,
                    erro = viewModel.erroPreco
                )
            }

            TituloSecao("Gravação")
            CampoTexto(
                "Texto da gravação", viewModel.textoGravacao, { viewModel.textoGravacao = it },
                Modifier.fillMaxWidth(), dica = "Deixe vazio se não tiver gravação"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoSelecao(
                    "Técnica", OpcoesPedido.TECNICAS, viewModel.tecnica,
                    { viewModel.tecnica = it }, Modifier.weight(1f)
                )
                CampoSelecao(
                    "Posição", OpcoesPedido.POSICOES, viewModel.posicao,
                    { viewModel.posicao = it }, Modifier.weight(1f)
                )
            }
            CampoTexto("Cor", viewModel.cor, { viewModel.cor = it }, Modifier.fillMaxWidth(), dica = "Ex.: Prata")

            TituloSecao("Pagamento")
            CampoSelecao(
                "Prazo", OpcoesPedido.PRAZOS, viewModel.prazo,
                { viewModel.prazo = it }, Modifier.fillMaxWidth(), textoDaOpcao = { it.rotulo }
            )
            PreviaParcelas(viewModel)

            TituloSecao("Entrega")
            CampoSelecao(
                "Frete", Frete.entries, viewModel.frete,
                { viewModel.frete = it }, Modifier.fillMaxWidth(), textoDaOpcao = { it.rotulo }
            )
            CampoData(
                "Previsão de despacho", viewModel.previsaoDespacho,
                { viewModel.previsaoDespacho = it }, Modifier.fillMaxWidth()
            )
            CampoTexto(
                "Observações", viewModel.observacoes, { viewModel.observacoes = it },
                Modifier.fillMaxWidth()
            )
        }

        BarraTotal(viewModel.total, viewModel.salvando) { viewModel.salvar(aoSalvar) }
    }
}

@Composable
private fun PreviaParcelas(viewModel: NovoPedidoViewModel) {
    val previsao = viewModel.previsaoDespacho
    Painel(titulo = "Parcelas (geradas automaticamente)") {
        if (previsao == null || viewModel.total == 0L) {
            Text(
                "Preencha quantidade, preço e previsão de despacho para ver as parcelas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Calculos.gerarParcelas(viewModel.total, previsao, viewModel.prazo.dias).forEach {
                LinhaInfo("${it.numero}ª · ${Formatos.data(it.vencimento)}", Formatos.moeda(it.valorCentavos))
            }
        }
        Text(
            "Vencimentos contados a partir da data de despacho.",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BarraTotal(total: Long, salvando: Boolean, aoSalvar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Total do pedido",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(Formatos.moeda(total), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                BotaoPrimario(
                    if (salvando) "Salvando..." else "Salvar pedido",
                    aoSalvar,
                    icone = R.drawable.ic_check,
                    habilitado = !salvando
                )
            }
        }
    }
}
