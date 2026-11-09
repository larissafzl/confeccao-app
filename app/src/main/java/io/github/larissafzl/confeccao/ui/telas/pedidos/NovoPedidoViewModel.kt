package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.dados.ClienteRepositorio
import io.github.larissafzl.confeccao.dados.Frete
import io.github.larissafzl.confeccao.dados.Gravacao
import io.github.larissafzl.confeccao.dados.ModeloCapa
import io.github.larissafzl.confeccao.dados.ModeloRepositorio
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.PedidoRepositorio
import io.github.larissafzl.confeccao.navegacao.NovoPedido
import io.github.larissafzl.confeccao.util.Formatos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class NovoPedidoViewModel(estado: SavedStateHandle) : ViewModel() {

    private val pedidos = PedidoRepositorio()

    val clientes: StateFlow<List<Cliente>> = ClienteRepositorio().observarTodos()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val modelos: StateFlow<List<ModeloCapa>> = ModeloRepositorio().observarTodos()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var clienteId by mutableStateOf(estado.toRoute<NovoPedido>().clienteId.orEmpty())
    var modeloId by mutableStateOf("")
        private set
    var quantidadeTexto by mutableStateOf("")
    var precoTexto by mutableStateOf("")
    var textoGravacao by mutableStateOf("")
    var tecnica by mutableStateOf(OpcoesPedido.TECNICAS.first())
    var posicao by mutableStateOf(OpcoesPedido.POSICOES.first())
    var cor by mutableStateOf("")
    var prazo by mutableStateOf(OpcoesPedido.PRAZOS.first())
    var frete by mutableStateOf(Frete.CLIENTE)
    var previsaoDespacho by mutableStateOf<LocalDate?>(null)
    var observacoes by mutableStateOf("")

    var erroCliente by mutableStateOf<String?>(null)
        private set
    var erroModelo by mutableStateOf<String?>(null)
        private set
    var erroQuantidade by mutableStateOf<String?>(null)
        private set
    var erroPreco by mutableStateOf<String?>(null)
        private set
    var salvando by mutableStateOf(false)
        private set

    val quantidade: Int? get() = quantidadeTexto.trim().toIntOrNull()?.takeIf { it > 0 }
    val preco: Long? get() = Formatos.lerMoeda(precoTexto)
    val total: Long get() = (quantidade ?: 0) * (preco ?: 0)

    fun escolherModelo(modelo: ModeloCapa) {
        modeloId = modelo.id
        precoTexto = Formatos.moeda(modelo.precoCentavos).removePrefix("R$ ")
    }

    fun salvar(aoSalvar: (String) -> Unit) {
        val cliente = clientes.value.find { it.id == clienteId }
        val modelo = modelos.value.find { it.id == modeloId }
        erroCliente = if (cliente == null) "Escolha o cliente." else null
        erroModelo = if (modelo == null) "Escolha o modelo." else null
        erroQuantidade = if (quantidade == null) "Informe a quantidade." else null
        erroPreco = if (preco == null) "Informe o preço." else null
        if (cliente == null || modelo == null || erroQuantidade != null || erroPreco != null || salvando) return

        salvando = true
        viewModelScope.launch {
            val ultimo = pedidos.observarTodos().catch { emit(emptyList()) }.first().maxOfOrNull { it.numero } ?: 0
            val id = pedidos.salvar(
                Pedido(
                    numero = ultimo + 1,
                    clienteId = cliente.id,
                    clienteNome = cliente.nome,
                    modeloId = modelo.id,
                    modeloNome = modelo.nome,
                    quantidade = quantidade ?: 0,
                    precoUnitarioCentavos = preco ?: 0,
                    gravacao = textoGravacao.trim().takeIf { it.isNotEmpty() }?.let {
                        Gravacao(texto = it, tecnica = tecnica, posicao = posicao)
                    },
                    cor = cor.trim(),
                    prazosDias = prazo.dias,
                    frete = frete,
                    previsaoDespacho = previsaoDespacho,
                    observacoes = observacoes.trim()
                )
            )
            salvando = false
            aoSalvar(id)
        }
    }
}
