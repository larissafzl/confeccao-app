package io.github.larissafzl.confeccao.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.ui.theme.AlertaFundo
import io.github.larissafzl.confeccao.ui.theme.AlertaTexto
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme
import io.github.larissafzl.confeccao.ui.theme.InfoFundo
import io.github.larissafzl.confeccao.ui.theme.InfoTexto
import io.github.larissafzl.confeccao.ui.theme.VerdeEscuro
import io.github.larissafzl.confeccao.ui.theme.VerdeSuave

private val FormatoCartao = RoundedCornerShape(16.dp)

enum class TipoChip(val fundo: Color, val texto: Color) {
    ALERTA(AlertaFundo, AlertaTexto),
    INFO(InfoFundo, InfoTexto),
    SUCESSO(VerdeSuave, VerdeEscuro)
}

@Composable
fun ChipStatus(texto: String, tipo: TipoChip, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(tipo.fundo, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier
                .size(6.dp)
                .background(tipo.texto, CircleShape)
        )
        Text(texto, color = tipo.texto, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun Cartao(
    modifier: Modifier = Modifier,
    aoClicar: (() -> Unit)? = null,
    conteudo: @Composable () -> Unit
) {
    val borda = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    val cor = MaterialTheme.colorScheme.surface
    if (aoClicar != null) {
        Surface(onClick = aoClicar, modifier, shape = FormatoCartao, color = cor, border = borda, content = conteudo)
    } else {
        Surface(modifier, shape = FormatoCartao, color = cor, border = borda, content = conteudo)
    }
}

@Composable
fun CardIndicador(
    @DrawableRes icone: Int,
    valor: String,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    Cartao(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(icone),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                rotulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ItemPedido(
    cliente: String,
    detalhe: String,
    data: String,
    valor: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    status: @Composable () -> Unit = {}
) {
    Cartao(modifier.fillMaxWidth(), aoClicar) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(cliente, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(detalhe, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(data, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                status()
            }
        }
    }
}

@Composable
fun ItemLista(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    iniciais: String? = null,
    valor: String? = null,
    aoClicar: (() -> Unit)? = null,
    mostrarSeta: Boolean = aoClicar != null,
    fundoIniciais: Color = MaterialTheme.colorScheme.primaryContainer,
    corIniciais: Color = MaterialTheme.colorScheme.primary
) {
    Cartao(modifier.fillMaxWidth(), aoClicar) {
        Row(
            Modifier.padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (iniciais != null) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(fundoIniciais, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iniciais, color = corIniciais, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (valor != null) {
                Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }
            if (mostrarSeta) {
                Icon(
                    painterResource(R.drawable.ic_seta_direita),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BotaoFlutuante(texto: String, aoClicar: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        onClick = aoClicar,
        modifier = modifier,
        icon = { Icon(painterResource(R.drawable.ic_mais), contentDescription = null) },
        text = { Text(texto, style = MaterialTheme.typography.labelLarge) },
        shape = FormatoCartao,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun <T> FiltroChips(
    opcoes: List<T>,
    selecionada: T,
    aoSelecionar: (T) -> Unit,
    rotulo: (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        opcoes.forEach { opcao ->
            FilterChip(
                selected = opcao == selecionada,
                onClick = { aoSelecionar(opcao) },
                label = { Text(rotulo(opcao)) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun EstadoVazio(titulo: String, mensagem: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Text(
            mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListasPreview() {
    ConfeccaoTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardIndicador(R.drawable.ic_pedidos, "12", "Pedidos no mês")
            ItemPedido("Cliente exemplo", "300 un. · Mod. 05", "10/11/2026", "R$ 2.370,00", {}) {
                ChipStatus("Em produção", TipoChip.ALERTA)
            }
            ItemLista("Cliente exemplo", "Sumaré/SP", iniciais = "CE", aoClicar = {})
            BotaoFlutuante("Novo pedido", {})
        }
    }
}
