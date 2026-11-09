package io.github.larissafzl.confeccao.ui.telas.modelos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.ModeloCapa
import io.github.larissafzl.confeccao.dados.ModeloRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

class ModelosViewModel : ViewModel() {

    private val repositorio = ModeloRepositorio()

    val modelos: StateFlow<List<ModeloCapa>?> = repositorio.observarTodos()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
