package io.github.larissafzl.confeccao.ui.telas.modelos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.ModeloCapa
import io.github.larissafzl.confeccao.dados.ModeloRepositorio
import io.github.larissafzl.confeccao.navegacao.EditarModelo
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditarModeloViewModel(estado: SavedStateHandle) : ViewModel() {

    private val repositorio = ModeloRepositorio()

    private val modeloId = estado.toRoute<EditarModelo>().modeloId
    val editando = modeloId != null

    var nome by mutableStateOf("")
    var descricao by mutableStateOf("")
    var custoTexto by mutableStateOf("")
    var precoTexto by mutableStateOf("")
    var carregando by mutableStateOf(editando)
        private set
    var erroNome by mutableStateOf<String?>(null)
        private set
    var erroCusto by mutableStateOf<String?>(null)
        private set
    var erroPreco by mutableStateOf<String?>(null)
        private set

    val custo: Long? get() = if (custoTexto.isBlank()) 0 else Formatos.lerMoeda(custoTexto)
    val preco: Long? get() = Formatos.lerMoeda(precoTexto)

    init {
        if (modeloId != null) {
            viewModelScope.launch {
                repositorio.observar(modeloId).first()?.let {
                    nome = it.nome
                    descricao = it.descricao
                    custoTexto = paraTexto(it.custoCentavos)
                    precoTexto = paraTexto(it.precoCentavos)
                }
                carregando = false
            }
        }
    }

    fun salvar(aoSalvar: () -> Unit) {
        erroNome = if (nome.isBlank()) "Informe o nome do modelo." else null
        erroCusto = if (custo == null) "Valor inválido." else null
        erroPreco = when {
            precoTexto.isBlank() -> "Informe o preço."
            preco == null -> "Valor inválido."
            else -> null
        }
        if (erroNome != null || erroCusto != null || erroPreco != null) return

        repositorio.salvar(
            ModeloCapa(
                id = modeloId.orEmpty(),
                nome = nome.trim(),
                descricao = descricao.trim(),
                custoCentavos = custo ?: 0,
                precoCentavos = preco ?: 0
            )
        )
        aoSalvar()
    }

    fun excluir(aoExcluir: () -> Unit) {
        modeloId?.let { repositorio.excluir(it) }
        aoExcluir()
    }

    private fun paraTexto(centavos: Long) = Formatos.moeda(centavos).removePrefix("R$ ")
}
