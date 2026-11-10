package io.github.larissafzl.confeccao.ui.telas.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.ResumoMensal
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.BotaoSecundario
import io.github.larissafzl.confeccao.ui.componentes.CardIndicador
import io.github.larissafzl.confeccao.ui.componentes.Cartao
import io.github.larissafzl.confeccao.ui.componentes.MenuOpcoes
import io.github.larissafzl.confeccao.ui.componentes.TituloSecao
import io.github.larissafzl.confeccao.ui.telas.pedidos.CartaoPedido
import io.github.larissafzl.confeccao.util.Formatos
import java.time.YearMonth

@Composable
fun TelaInicio(
    aoNovoPedido: () -> Unit,
    aoAbrirReciclagem: () -> Unit,
    aoVerPedidos: () -> Unit,
    aoAbrirPedido: (String) -> Unit,
    aoAbrirModelos: () -> Unit,
    aoSair: () -> Unit,
    viewModel: InicioViewModel = viewModel()
) {
    val resumo by viewModel.resumo.collectAsStateWithLifecycle()
    val mes by viewModel.mes.collectAsStateWithLifecycle()
    val ultimos by viewModel.ultimosPedidos.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        BarraSuperior(
            stringResource(R.string.app_name),
            acoes = { MenuOpcoes(listOf("Modelos de capa" to aoAbrirModelos, "Sair" to aoSair)) }
        )

        val dados = resumo
        if (dados == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeletorMes(mes, viewModel.mesesDisponiveis) { viewModel.mes.value = it }
            Indicadores(dados)
            CartaoAReceber(dados)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BotaoPrimario("Novo pedido", aoNovoPedido, Modifier.weight(1f), icone = R.drawable.ic_mais)
                BotaoSecundario("Reciclagem", aoAbrirReciclagem, Modifier.weight(1f), icone = R.drawable.ic_reciclagem)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TituloSecao("Últimos pedidos", Modifier.weight(1f))
                TextButton(onClick = aoVerPedidos) { Text("Ver todos") }
            }
            if (ultimos.isEmpty()) {
                Text(
                    "Nenhum pedido ainda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                ultimos.forEach { pedido ->
                    CartaoPedido(pedido, pedido.clienteNome) { aoAbrirPedido(pedido.id) }
                }
            }
        }
    }
}

@Composable
private fun SeletorMes(mes: YearMonth, opcoes: List<YearMonth>, aoEscolher: (YearMonth) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { aberto = true },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                Modifier.padding(start = 12.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_calendario),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(Formatos.mesAno(mes), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Icon(
                    painterResource(R.drawable.ic_seta_baixo),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(Formatos.mesAno(opcao)) },
                    onClick = {
                        aberto = false
                        aoEscolher(opcao)
                    }
                )
            }
        }
    }
}

@Composable
private fun Indicadores(dados: ResumoMensal) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CardIndicador(
                R.drawable.ic_caixa,
                Formatos.numero(dados.capasVendidas.toDouble()),
                "Capas vendidas",
                Modifier.weight(1f)
            )
            CardIndicador(
                R.drawable.ic_dinheiro,
                Formatos.moedaCurta(dados.faturamentoCentavos),
                "Faturamento",
                Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CardIndicador(
                R.drawable.ic_grafico,
                Formatos.moedaCurta(dados.lucroEstimadoCentavos),
                "Lucro estimado",
                Modifier.weight(1f)
            )
            CardIndicador(
                R.drawable.ic_folha,
                "${Formatos.numero(dados.quilosReciclados)} kg",
                "Reciclado · ${Formatos.moeda(dados.reciclagemCentavos)}",
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CartaoAReceber(dados: ResumoMensal) {
    Cartao(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_calendario),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "A receber",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(Formatos.moeda(dados.aReceberCentavos), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                when (dados.parcelasAReceber) {
                    0 -> "Nada em aberto"
                    1 -> "1 parcela"
                    else -> "${dados.parcelasAReceber} parcelas"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
