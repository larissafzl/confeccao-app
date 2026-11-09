package io.github.larissafzl.confeccao.ui.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

// Tela temporária
@Composable
fun TelaProvisoria(
    titulo: String,
    atalhos: List<Pair<String, () -> Unit>> = emptyList(),
    barraSuperior: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        barraSuperior?.invoke()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (barraSuperior == null) {
                Text(text = titulo, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = "Em construção",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            atalhos.forEach { (rotulo, acao) ->
                TextButton(onClick = acao) { Text(rotulo) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TelaProvisoriaPreview() {
    ConfeccaoTheme {
        TelaProvisoria(
            "Pedidos",
            listOf("Novo pedido" to {}, "Detalhe do pedido" to {}),
            barraSuperior = { BarraSuperior("Pedidos") }
        )
    }
}
