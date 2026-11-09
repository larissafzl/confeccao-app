package io.github.larissafzl.confeccao.ui.telas.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ClientesViewModel(repositorio: ClienteRepositorio = ClienteRepositorio()) : ViewModel() {

    val busca = MutableStateFlow("")

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
