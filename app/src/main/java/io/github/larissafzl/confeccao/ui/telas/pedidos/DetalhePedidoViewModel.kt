package io.github.larissafzl.confeccao.ui.telas.pedidos

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.navegacao.DetalhePedido
import io.github.larissafzl.confeccao.pdf.GeradorPdfPedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface EstadoDetalhePedido {
    data object Carregando : EstadoDetalhePedido
    data object NaoEncontrado : EstadoDetalhePedido
    data class Pronto(val pedido: Pedido) : EstadoDetalhePedido
}

class DetalhePedidoViewModel(estado: SavedStateHandle) : ViewModel() {

    private val repositorio = PedidoRepositorio()

    val pedidoId = estado.toRoute<DetalhePedido>().pedidoId

    val tela: StateFlow<EstadoDetalhePedido> = repositorio.observar(pedidoId)
        .map { if (it == null) EstadoDetalhePedido.NaoEncontrado else EstadoDetalhePedido.Pronto(it) }
        .catch { emit(EstadoDetalhePedido.NaoEncontrado) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoDetalhePedido.Carregando)

    fun alternarPaga(pedido: Pedido, numero: Int) {
        repositorio.salvar(
            pedido.copy(parcelas = pedido.parcelas.map { if (it.numero == numero) it.copy(paga = !it.paga) else it })
        )
    }

    var gerandoPdf by mutableStateOf(false)
        private set

    fun gerarPdf(context: Context, pedido: Pedido, aoGerar: (File) -> Unit) {
        if (gerandoPdf) return
        gerandoPdf = true
        viewModelScope.launch {
            val cliente = ClienteRepositorio().observar(pedido.clienteId).catch { emit(null) }.first()
            val arquivo = withContext(Dispatchers.IO) {
                GeradorPdfPedido(context.applicationContext).gerar(pedido, cliente)
            }
            gerandoPdf = false
            aoGerar(arquivo)
        }
    }

    fun excluir(aoExcluir: () -> Unit) {
        repositorio.excluir(pedidoId)
        aoExcluir()
    }
}
