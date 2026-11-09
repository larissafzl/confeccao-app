package io.github.larissafzl.confeccao.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = RoxoMarca,
    onPrimary = Color.White,
    primaryContainer = RoxoSuave,
    onPrimaryContainer = RoxoMarca,
    secondary = VerdeEscuro,
    onSecondary = Color.White,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = VerdeEscuro,
    background = Fundo,
    onBackground = Texto,
    surface = Superficie,
    onSurface = Texto,
    surfaceVariant = Fundo,
    onSurfaceVariant = TextoSecundario,
    outline = Borda,
    outlineVariant = Borda
)

@Composable
fun ConfeccaoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Tipografia,
        content = content
    )
}
