package io.github.larissafzl.confeccao.ui.telas.clientes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.navegacao.DetalheCliente
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface EstadoDetalheCliente {
    data object Carregando : EstadoDetalheCliente
    data object NaoEncontrado : EstadoDetalheCliente
    data class Pronto(val cliente: Cliente) : EstadoDetalheCliente
}

class DetalheClienteViewModel(estado: SavedStateHandle) : ViewModel() {

    private val repositorio = ClienteRepositorio()

    val clienteId = estado.toRoute<DetalheCliente>().clienteId

    val tela: StateFlow<EstadoDetalheCliente> = repositorio.observar(clienteId)
        .map { cliente ->
            if (cliente == null) EstadoDetalheCliente.NaoEncontrado else EstadoDetalheCliente.Pronto(cliente)
        }
        .catch { emit(EstadoDetalheCliente.NaoEncontrado) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoDetalheCliente.Carregando)

    val pedidos: StateFlow<List<Pedido>> = PedidoRepositorio().observarDoCliente(clienteId)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
