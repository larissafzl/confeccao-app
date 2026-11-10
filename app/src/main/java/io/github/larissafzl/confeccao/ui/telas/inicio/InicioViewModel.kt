package io.github.larissafzl.confeccao.ui.telas.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.ModeloRepositorio
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.dados.ReciclagemRepositorio
import io.github.larissafzl.confeccao.dados.ResumoMensal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

class InicioViewModel : ViewModel() {

    private val pedidos = PedidoRepositorio().observarTodos().catch { emit(emptyList()) }
    private val modelos = ModeloRepositorio().observarTodos().catch { emit(emptyList()) }
    private val reciclagens = ReciclagemRepositorio().observarTodos().catch { emit(emptyList()) }

    val mes = MutableStateFlow(YearMonth.now())

    // Os últimos 12 meses, do atual para trás
    val mesesDisponiveis: List<YearMonth> = (0L until 12L).map { YearMonth.now().minusMonths(it) }

    val resumo: StateFlow<ResumoMensal?> = combine(pedidos, modelos, reciclagens, mes) { p, m, r, mesAtual ->
        ResumoMensal.calcular(mesAtual, p, m, r)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val ultimosPedidos: StateFlow<List<Pedido>> = pedidos
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
