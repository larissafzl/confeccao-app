package io.github.larissafzl.confeccao.ui.telas.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClientesViewModel : ViewModel() {

    private val repositorio = ClienteRepositorio()

    val busca = MutableStateFlow("")

    val pedidosPorCliente: StateFlow<Map<String, Int>> = PedidoRepositorio().observarTodos()
        .map { pedidos -> pedidos.groupingBy { it.clienteId }.eachCount() }
        .catch { emit(emptyMap()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    // null enquanto carrega
    val clientes: StateFlow<List<Cliente>?> = combine(
        repositorio.observarTodos().catch { emit(emptyList()) },
        busca
    ) { lista, termo -> filtrar(lista, termo) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private fun filtrar(lista: List<Cliente>, termo: String): List<Cliente> {
        if (termo.isBlank()) return lista
        val procurado = Formatos.normalizar(termo.trim())
        return lista.filter {
            Formatos.normalizar(it.nome).contains(procurado) ||
                Formatos.normalizar(it.cidade).contains(procurado)
        }
    }
}
