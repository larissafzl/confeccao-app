package io.github.larissafzl.confeccao.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

@Composable
fun Painel(
    modifier: Modifier = Modifier,
    titulo: String? = null,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Cartao(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (titulo != null) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
            }
            conteudo()
        }
    }
}

@Composable
fun LinhaInfo(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun TituloSecao(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        modifier = modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(showBackground = true)
@Composable
private fun PainelPreview() {
    ConfeccaoTheme {
        Painel(Modifier.padding(16.dp), titulo = "Dados do cliente") {
            LinhaInfo("CNPJ", "00.000.000/0001-00")
            LinhaInfo("Telefone", "(19) 0000-0000")
        }
    }
}
