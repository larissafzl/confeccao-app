package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoFlutuante
import io.github.larissafzl.confeccao.ui.componentes.CampoBusca
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.FiltroChips

@Composable
fun TelaPedidos(
    aoAbrirPedido: (String) -> Unit,
    aoNovoPedido: () -> Unit,
    viewModel: PedidosViewModel = viewModel()
) {
    val pedidos by viewModel.pedidos.collectAsStateWithLifecycle()
    val busca by viewModel.busca.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            BarraSuperior("Pedidos")
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CampoBusca(busca, { viewModel.busca.value = it }, "Buscar por cliente")
                }
                item {
                    FiltroChips(
                        FiltroPedidos.entries,
                        filtro,
                        { viewModel.filtro.value = it },
                        { it.rotulo }
                    )
                }
                val lista = pedidos
                when {
                    lista == null -> item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    lista.isEmpty() && busca.isBlank() && filtro == FiltroPedidos.TODOS -> item {
                        EstadoVazio("Nenhum pedido ainda", "Toque em Novo pedido para registrar o primeiro.")
                    }
                    lista.isEmpty() -> item {
                        EstadoVazio("Nenhum pedido encontrado", "Tente outro filtro ou outro nome de cliente.")
                    }
                    else -> items(lista, key = { it.id }) { pedido ->
                        CartaoPedido(pedido, pedido.clienteNome) { aoAbrirPedido(pedido.id) }
                    }
                }
            }
        }
        BotaoFlutuante(
            "Novo pedido",
            aoNovoPedido,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
