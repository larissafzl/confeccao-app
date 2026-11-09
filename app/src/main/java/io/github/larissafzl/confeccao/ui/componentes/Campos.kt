package io.github.larissafzl.confeccao.ui.componentes

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

private val FormatoCampo = RoundedCornerShape(12.dp)

@Composable
private fun coresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
private fun Rotulo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    dica: String = "",
    tipoTeclado: KeyboardType = KeyboardType.Text,
    erro: String? = null,
    senha: Boolean = false
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Rotulo(rotulo)
        OutlinedTextField(
            value = valor,
            onValueChange = aoMudar,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (dica.isNotEmpty()) ({ Text(dica) }) else null,
            singleLine = true,
            isError = erro != null,
            supportingText = erro?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = if (senha) KeyboardType.Password else tipoTeclado),
            visualTransformation = if (senha) PasswordVisualTransformation() else VisualTransformation.None,
            shape = FormatoCampo,
            colors = coresCampo()
        )
    }
}

@Composable
fun <T> CampoSelecao(
    rotulo: String,
    opcoes: List<T>,
    selecionada: T?,
    aoSelecionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    dica: String = "Selecione",
    textoDaOpcao: (T) -> String = { it.toString() }
) {
    var aberto by remember { mutableStateOf(false) }
    val interacao = remember { MutableInteractionSource() }
    LaunchedEffect(interacao) {
        interacao.interactions.collect { if (it is PressInteraction.Release) aberto = true }
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Rotulo(rotulo)
        Box {
            OutlinedTextField(
                value = selecionada?.let(textoDaOpcao).orEmpty(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                placeholder = { Text(dica) },
                trailingIcon = {
                    Icon(painterResource(R.drawable.ic_seta_baixo), contentDescription = null)
                },
                singleLine = true,
                shape = FormatoCampo,
                colors = coresCampo(),
                interactionSource = interacao
            )
            DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
                opcoes.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(textoDaOpcao(opcao)) },
                        onClick = {
                            aberto = false
                            aoSelecionar(opcao)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CamposPreview() {
    ConfeccaoTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CampoTexto("Quantidade", "300", {}, tipoTeclado = KeyboardType.Number)
            CampoSelecao("Modelo", listOf("Mod. 01", "Mod. 05"), "Mod. 05", {})
        }
    }
}
