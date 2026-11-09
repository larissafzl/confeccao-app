package io.github.larissafzl.confeccao.ui.telas.pedidos

import androidx.compose.runtime.Composable
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.dados.Pedido
import io.github.larissafzl.confeccao.dados.StatusPedido
import io.github.larissafzl.confeccao.ui.componentes.ChipStatus
import io.github.larissafzl.confeccao.ui.componentes.ItemPedido
import io.github.larissafzl.confeccao.ui.componentes.TipoChip
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun ChipStatusPedido(status: StatusPedido) {
    val tipo = if (status == StatusPedido.DESPACHADO) TipoChip.INFO else TipoChip.ALERTA
    ChipStatus(status.rotulo, tipo)
}

fun Pedido.resumoProduto(): String = "$modeloNome · $quantidade capas"

fun Pedido.situacaoEntrega(): String = when {
    despacho != null -> "Despachado em ${Formatos.data(despacho.data)}"
    previsaoDespacho != null -> "Despacho prev. ${Formatos.data(previsaoDespacho)}"
    else -> "Criado em ${Formatos.data(criadoEm)}"
}

@Composable
fun CartaoPedido(pedido: Pedido, titulo: String, aoClicar: () -> Unit) {
    ItemPedido(
        cliente = titulo,
        detalhe = pedido.resumoProduto(),
        data = pedido.situacaoEntrega(),
        valor = Formatos.moeda(Calculos.totalPedido(pedido)),
        aoClicar = aoClicar
    ) {
        ChipStatusPedido(pedido.status)
    }
}
