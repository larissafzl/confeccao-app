package io.github.larissafzl.confeccao.ui.telas.reciclagem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.Reciclagem
import io.github.larissafzl.confeccao.dados.ReciclagemRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

data class ResumoMes(
    val mes: YearMonth,
    val registros: List<Reciclagem>,
    val quilos: Double,
    val valorCentavos: Long
)

class ReciclagemViewModel : ViewModel() {

    private val repositorio = ReciclagemRepositorio()

    val mes = MutableStateFlow(YearMonth.now())

    val resumo: StateFlow<ResumoMes?> = combine(
        repositorio.observarTodos().catch { emit(emptyList()) },
        mes
    ) { todos, mesAtual ->
        val doMes = todos.filter { YearMonth.from(it.data) == mesAtual }
        ResumoMes(
            mes = mesAtual,
            registros = doMes,
            quilos = doMes.filter { it.unidade == "kg" }.sumOf { it.quantidade },
            valorCentavos = doMes.sumOf { it.valorCentavos }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun mesAnterior() {
        mes.value = mes.value.minusMonths(1)
    }

    fun proximoMes() {
        if (mes.value < YearMonth.now()) mes.value = mes.value.plusMonths(1)
    }

    fun excluir(id: String) = repositorio.excluir(id)
}
