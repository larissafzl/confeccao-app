package io.github.larissafzl.confeccao.ui.telas.clientes

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
import io.github.larissafzl.confeccao.ui.componentes.ItemLista
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaClientes(
    aoAbrirCliente: (String) -> Unit,
    aoNovoCliente: () -> Unit,
    viewModel: ClientesViewModel = viewModel()
) {
    val clientes by viewModel.clientes.collectAsStateWithLifecycle()
    val busca by viewModel.busca.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            BarraSuperior("Clientes")
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CampoBusca(busca, { viewModel.busca.value = it }, "Buscar cliente")
                }
                val lista = clientes
                when {
                    lista == null -> item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    lista.isEmpty() && busca.isBlank() -> item {
                        EstadoVazio("Nenhum cliente cadastrado", "Toque em Novo cliente para começar.")
                    }
                    lista.isEmpty() -> item {
                        EstadoVazio("Nenhum cliente encontrado", "Confira o nome ou a cidade digitados.")
                    }
                    else -> items(lista, key = { it.id }) { cliente ->
                        ItemLista(
                            titulo = cliente.nome,
                            subtitulo = cliente.local,
                            iniciais = Formatos.iniciais(cliente.nome),
                            aoClicar = { aoAbrirCliente(cliente.id) }
                        )
                    }
                }
            }
        }
        BotaoFlutuante(
            "Novo cliente",
            aoNovoCliente,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
