package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.CampoData
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.LinhaInfo
import io.github.larissafzl.confeccao.ui.componentes.Painel
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaDespachar(
    aoVoltar: () -> Unit,
    viewModel: DespacharViewModel = viewModel()
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior("Despachar pedido", aoVoltar = aoVoltar)

        val pedido = viewModel.pedido
        when {
            viewModel.carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            pedido == null -> EstadoVazio("Pedido não encontrado", "Volte e abra o pedido de novo.")
            else -> Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val total = Calculos.totalPedido(pedido)
                Painel {
                    Text(pedido.clienteNome, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${pedido.resumoProduto()} · ${Formatos.moeda(total)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoTexto(
                        "Volumes", viewModel.volumesTexto, { viewModel.volumesTexto = it },
                        Modifier.weight(1f), dica = "Caixas", tipoTeclado = KeyboardType.Number
                    )
                    CampoTexto(
                        "Peso total (kg)", viewModel.pesoTexto, { viewModel.pesoTexto = it },
                        Modifier.weight(1f), tipoTeclado = KeyboardType.Decimal
                    )
                }
                CampoTexto(
                    "Medidas da caixa", viewModel.medidas, { viewModel.medidas = it },
                    Modifier.fillMaxWidth(), dica = "Ex.: 49 x 33 x 43 cm"
                )
                CampoTexto(
                    "Transportadora", viewModel.transportadora, { viewModel.transportadora = it },
                    Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoTexto(
                        "Valor do frete", viewModel.freteTexto, { viewModel.freteTexto = it },
                        Modifier.weight(1f), dica = "0,00", tipoTeclado = KeyboardType.Decimal,
                        erro = viewModel.erroFrete
                    )
                    CampoTexto(
                        "Nota fiscal", viewModel.notaFiscal, { viewModel.notaFiscal = it },
                        Modifier.weight(1f), dica = "Nº", tipoTeclado = KeyboardType.Number
                    )
                }
                CampoData("Data de despacho", viewModel.data, { viewModel.data = it }, Modifier.fillMaxWidth())

                Painel(titulo = "Parcelas que serão geradas") {
                    Calculos.gerarParcelas(total, viewModel.data, pedido.prazosDias).forEach {
                        LinhaInfo("${it.numero}ª · ${Formatos.data(it.vencimento)}", Formatos.moeda(it.valorCentavos))
                    }
                }

                BotaoPrimario(
                    "Confirmar despacho",
                    { viewModel.confirmar(aoVoltar) },
                    Modifier.fillMaxWidth(),
                    icone = R.drawable.ic_check
                )
            }
        }
    }
}
