package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.dados.StatusPedido
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class FiltroPedidos(val rotulo: String, val status: StatusPedido?) {
    TODOS("Todos", null),
    EM_PRODUCAO("Em produção", StatusPedido.EM_PRODUCAO),
    DESPACHADOS("Despachados", StatusPedido.DESPACHADO)
}

class PedidosViewModel : ViewModel() {

    private val repositorio = PedidoRepositorio()

    val busca = MutableStateFlow("")
    val filtro = MutableStateFlow(FiltroPedidos.TODOS)

    val pedidos: StateFlow<List<Pedido>?> = combine(
        repositorio.observarTodos().catch { emit(emptyList()) },
        busca,
        filtro
    ) { lista, termo, filtroAtual ->
        val procurado = Formatos.normalizar(termo.trim())
        lista.filter { pedido ->
            (filtroAtual.status == null || pedido.status == filtroAtual.status) &&
                (procurado.isEmpty() || Formatos.normalizar(pedido.clienteNome).contains(procurado))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
