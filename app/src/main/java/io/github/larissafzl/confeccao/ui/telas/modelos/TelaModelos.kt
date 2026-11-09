package io.github.larissafzl.confeccao.ui.telas.modelos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoFlutuante
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.ItemLista
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaModelos(
    aoVoltar: () -> Unit,
    aoAbrirModelo: (String) -> Unit,
    aoNovoModelo: () -> Unit,
    viewModel: ModelosViewModel = viewModel()
) {
    val modelos by viewModel.modelos.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            BarraSuperior("Modelos", aoVoltar = aoVoltar)
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Custo e preço de cada modelo de capa. O lucro é calculado automaticamente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val lista = modelos
                when {
                    lista == null -> item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    lista.isEmpty() -> item {
                        EstadoVazio("Nenhum modelo cadastrado", "Toque em Novo modelo para cadastrar o primeiro.")
                    }
                    else -> items(lista, key = { it.id }) { modelo ->
                        ItemLista(
                            titulo = modelo.nome,
                            subtitulo = modelo.descricao,
                            iniciais = iniciaisDoModelo(modelo.nome),
                            valor = Formatos.moeda(modelo.precoCentavos),
                            aoClicar = { aoAbrirModelo(modelo.id) }
                        )
                    }
                }
            }
        }
        BotaoFlutuante(
            "Novo modelo",
            aoNovoModelo,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

// "Mod. 05" vira "05", como no protótipo
private fun iniciaisDoModelo(nome: String): String =
    nome.filter { it.isDigit() }.take(3).ifEmpty { Formatos.iniciais(nome) }
