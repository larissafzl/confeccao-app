package io.github.larissafzl.confeccao.ui.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

@Composable
fun BarraSuperior(
    titulo: String,
    modifier: Modifier = Modifier,
    aoVoltar: (() -> Unit)? = null,
    acoes: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = if (aoVoltar != null) 4.dp else 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (aoVoltar != null) {
            IconButton(onClick = aoVoltar) {
                Icon(painterResource(R.drawable.ic_voltar), contentDescription = "Voltar")
            }
        }
        Text(
            text = titulo,
            modifier = Modifier.weight(1f),
            fontSize = if (aoVoltar != null) 20.sp else 24.sp,
            fontWeight = if (aoVoltar != null) FontWeight.SemiBold else FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        acoes()
    }
}

// Menu de três pontos
@Composable
fun MenuOpcoes(opcoes: List<Pair<String, () -> Unit>>) {
    var aberto by remember { mutableStateOf(false) }
    IconButton(onClick = { aberto = true }) {
        Icon(painterResource(R.drawable.ic_menu), contentDescription = "Mais opções")
    }
    DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
        opcoes.forEach { (rotulo, acao) ->
            DropdownMenuItem(
                text = { Text(rotulo) },
                onClick = {
                    aberto = false
                    acao()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BarraSuperiorPreview() {
    ConfeccaoTheme {
        BarraSuperior("Brassarott", acoes = { MenuOpcoes(listOf("Modelos de capa" to {})) })
    }
}

@Preview(showBackground = true)
@Composable
private fun BarraSuperiorVoltarPreview() {
    ConfeccaoTheme {
        BarraSuperior("Novo pedido", aoVoltar = {})
    }
}
