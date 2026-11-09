package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.dados.Despacho
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.navegacao.Despachar
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class DespacharViewModel(estado: SavedStateHandle) : ViewModel() {

    private val repositorio = PedidoRepositorio()
    private val pedidoId = estado.toRoute<Despachar>().pedidoId

    var pedido by mutableStateOf<Pedido?>(null)
        private set
    var carregando by mutableStateOf(true)
        private set

    var volumesTexto by mutableStateOf("")
    var pesoTexto by mutableStateOf("")
    var medidas by mutableStateOf("")
    var transportadora by mutableStateOf("")
    var freteTexto by mutableStateOf("")
    var notaFiscal by mutableStateOf("")
    var data by mutableStateOf<LocalDate>(LocalDate.now())

    var erroFrete by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            pedido = repositorio.observar(pedidoId).catch { emit(null) }.first()
            carregando = false
        }
    }

    fun confirmar(aoConfirmar: () -> Unit) {
        val atual = pedido ?: return
        val frete = if (freteTexto.isBlank()) 0 else Formatos.lerMoeda(freteTexto)
        erroFrete = if (frete == null) "Valor inválido." else null
        if (frete == null) return

        val despacho = Despacho(
            data = data,
            volumes = volumesTexto.trim().toIntOrNull() ?: 0,
            pesoKg = pesoTexto.trim().replace(",", ".").toDoubleOrNull() ?: 0.0,
            medidasCaixa = medidas.trim(),
            transportadora = transportadora.trim(),
            valorFreteCentavos = frete,
            notaFiscal = notaFiscal.trim()
        )
        repositorio.salvar(Calculos.despachar(atual, despacho))
        aoConfirmar()
    }
}
