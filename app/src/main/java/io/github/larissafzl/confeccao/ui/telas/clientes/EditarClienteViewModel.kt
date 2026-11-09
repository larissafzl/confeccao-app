package io.github.larissafzl.confeccao.ui.telas.clientes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.navegacao.EditarCliente
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditarClienteViewModel(estado: SavedStateHandle) : ViewModel() {

    private val repositorio = ClienteRepositorio()

    private val clienteId = estado.toRoute<EditarCliente>().clienteId
    val editando = clienteId != null

    var cliente by mutableStateOf(Cliente())
    var carregando by mutableStateOf(editando)
        private set
    var erroNome by mutableStateOf<String?>(null)
        private set

    init {
        if (clienteId != null) {
            viewModelScope.launch {
                repositorio.observar(clienteId).first()?.let { cliente = it }
                carregando = false
            }
        }
    }

    fun salvar(aoSalvar: () -> Unit) {
        val nome = cliente.nome.trim()
        if (nome.isEmpty()) {
            erroNome = "Informe o nome do cliente."
            return
        }
        repositorio.salvar(
            cliente.copy(
                nome = nome,
                cnpj = cliente.cnpj.trim(),
                inscricaoEstadual = cliente.inscricaoEstadual.trim(),
                endereco = cliente.endereco.trim(),
                cidade = cliente.cidade.trim(),
                telefone = cliente.telefone.trim(),
                email = cliente.email.trim()
            )
        )
        aoSalvar()
    }

    fun excluir(aoExcluir: () -> Unit) {
        clienteId?.let { repositorio.excluir(it) }
        aoExcluir()
    }
}
