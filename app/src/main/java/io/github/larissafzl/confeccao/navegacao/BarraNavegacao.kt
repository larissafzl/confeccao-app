package io.github.larissafzl.confeccao.navegacao

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

// Abas fixas da barra inferior
enum class Aba(val rotulo: String, @param:DrawableRes val icone: Int, val rota: Any) {
    INICIO("Início", R.drawable.ic_inicio, Inicio),
    PEDIDOS("Pedidos", R.drawable.ic_pedidos, Pedidos),
    CLIENTES("Clientes", R.drawable.ic_clientes, Clientes),
    RECICLAGEM("Reciclagem", R.drawable.ic_reciclagem, Reciclagem)
}

@Composable
fun BarraNavegacao(abaAtual: Aba?, aoSelecionar: (Aba) -> Unit) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            Aba.entries.forEach { aba ->
                NavigationBarItem(
                    selected = aba == abaAtual,
                    onClick = { aoSelecionar(aba) },
                    icon = { Icon(painterResource(aba.icone), contentDescription = null) },
                    label = { Text(aba.rotulo) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun BarraNavegacaoPreview() {
    ConfeccaoTheme {
        BarraNavegacao(abaAtual = Aba.PEDIDOS, aoSelecionar = {})
    }
}
