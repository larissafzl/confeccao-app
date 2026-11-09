package io.github.larissafzl.confeccao.ui.telas.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.LinhaInfo
import io.github.larissafzl.confeccao.ui.componentes.Painel
import io.github.larissafzl.confeccao.ui.componentes.TituloSecao
import io.github.larissafzl.confeccao.ui.telas.pedidos.CartaoPedido
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaDetalheCliente(
    aoVoltar: () -> Unit,
    aoEditar: (String) -> Unit,
    aoNovoPedido: (String) -> Unit,
    aoAbrirPedido: (String) -> Unit,
    viewModel: DetalheClienteViewModel = viewModel()
) {
    val tela by viewModel.tela.collectAsStateWithLifecycle()
    val pedidos by viewModel.pedidos.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        BarraSuperior(
            "Cliente",
            aoVoltar = aoVoltar,
            acoes = {
                if (tela is EstadoDetalheCliente.Pronto) {
                    TextButton(onClick = { aoEditar(viewModel.clienteId) }) { Text("Editar") }
                }
            }
        )

        when (val estado = tela) {
            EstadoDetalheCliente.Carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            EstadoDetalheCliente.NaoEncontrado -> EstadoVazio(
                "Cliente não encontrado",
                "Ele pode ter sido excluído em outro aparelho."
            )
            is EstadoDetalheCliente.Pronto -> ConteudoCliente(
                estado.cliente,
                pedidos,
                aoAbrirPedido,
                aoNovoPedido = { aoNovoPedido(viewModel.clienteId) }
            )
        }
    }
}

@Composable
private fun ConteudoCliente(
    cliente: Cliente,
    pedidos: List<Pedido>,
    aoAbrirPedido: (String) -> Unit,
    aoNovoPedido: () -> Unit
) {
    val dados = listOf(
        "CNPJ" to cliente.cnpj,
        "Inscrição estadual" to cliente.inscricaoEstadual,
        "Endereço" to cliente.endereco,
        "E-mail" to cliente.email,
        "Telefone" to cliente.telefone
    ).filter { it.second.isNotBlank() }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Painel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        Formatos.iniciais(cliente.nome),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(cliente.nome, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    if (cliente.local.isNotBlank()) {
                        Text(
                            cliente.local,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Painel(titulo = "Dados do cliente") {
            if (dados.isEmpty()) {
                Text(
                    "Nenhum dado cadastrado. Toque em Editar para completar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                dados.forEach { (rotulo, valor) -> LinhaInfo(rotulo, valor) }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TituloSecao("Histórico de pedidos", Modifier.weight(1f))
            if (pedidos.isNotEmpty()) {
                Text(
                    textoPedidos(pedidos.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (pedidos.isEmpty()) {
            Text(
                "Nenhum pedido deste cliente ainda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            pedidos.forEach { pedido ->
                CartaoPedido(pedido, "Pedido nº ${Formatos.numeroPedido(pedido.numero)}") {
                    aoAbrirPedido(pedido.id)
                }
            }
        }

        BotaoPrimario(
            "Novo pedido para este cliente",
            aoNovoPedido,
            Modifier.fillMaxWidth(),
            icone = R.drawable.ic_mais
        )
    }
}
