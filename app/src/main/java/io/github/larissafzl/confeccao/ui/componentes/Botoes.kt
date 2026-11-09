package io.github.larissafzl.confeccao.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

private val FormatoBotao = RoundedCornerShape(24.dp)
private val EspacamentoBotao = PaddingValues(horizontal = 24.dp)

@Composable
private fun ConteudoBotao(texto: String, @DrawableRes icone: Int?) {
    if (icone != null) {
        Icon(painterResource(icone), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
    }
    Text(texto, style = MaterialTheme.typography.labelLarge)
}

@Composable
fun BotaoPrimario(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icone: Int? = null,
    habilitado: Boolean = true
) {
    Button(
        onClick = aoClicar,
        modifier = modifier.height(48.dp),
        enabled = habilitado,
        shape = FormatoBotao,
        contentPadding = EspacamentoBotao
    ) {
        ConteudoBotao(texto, icone)
    }
}

@Composable
fun BotaoSecundario(
    texto: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icone: Int? = null,
    habilitado: Boolean = true
) {
    OutlinedButton(
        onClick = aoClicar,
        modifier = modifier.height(48.dp),
        enabled = habilitado,
        shape = FormatoBotao,
        contentPadding = EspacamentoBotao,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        ConteudoBotao(texto, icone)
    }
}

@Preview(showBackground = true)
@Composable
private fun BotoesPreview() {
    ConfeccaoTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BotaoPrimario("Salvar pedido", {}, Modifier.fillMaxWidth())
            BotaoSecundario("Cancelar", {})
        }
    }
}
