package io.github.larissafzl.confeccao.ui.telas.reciclagem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.larissafzl.confeccao.dados.Material
import io.github.larissafzl.confeccao.dados.Reciclagem
import io.github.larissafzl.confeccao.dados.ReciclagemRepositorio
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class RegistrarReciclagemViewModel : ViewModel() {

    private val repositorio = ReciclagemRepositorio()

    val unidades = listOf("kg", "unidades")

    var data by mutableStateOf<LocalDate>(LocalDate.now())
    var material by mutableStateOf(Material.RETALHOS)
    var quantidadeTexto by mutableStateOf("")
    var unidade by mutableStateOf(unidades.first())
    var comprador by mutableStateOf("")
    var valorTexto by mutableStateOf("")

    var erroQuantidade by mutableStateOf<String?>(null)
        private set
    var erroValor by mutableStateOf<String?>(null)
        private set

    init {
        // Costumam vender sempre para a mesma empresa, então o último comprador já vem preenchido
        viewModelScope.launch {
            val ultimo = repositorio.observarTodos().catch { emit(emptyList()) }.first().firstOrNull()
            if (comprador.isBlank() && ultimo != null) comprador = ultimo.comprador
        }
    }

    fun salvar(aoSalvar: () -> Unit) {
        val quantidade = quantidadeTexto.trim().replace(",", ".").toDoubleOrNull()?.takeIf { it > 0 }
        val valor = if (valorTexto.isBlank()) 0 else Formatos.lerMoeda(valorTexto)
        erroQuantidade = if (quantidade == null) "Informe a quantidade." else null
        erroValor = if (valor == null) "Valor inválido." else null
        if (quantidade == null || valor == null) return

        repositorio.salvar(
            Reciclagem(
                data = data,
                material = material,
                quantidade = quantidade,
                unidade = unidade,
                comprador = comprador.trim(),
                valorCentavos = valor
            )
        )
        aoSalvar()
    }
}
