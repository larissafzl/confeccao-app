package io.github.larissafzl.confeccao.ui.telas.reciclagem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Material
import io.github.larissafzl.confeccao.dados.Reciclagem
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoFlutuante
import io.github.larissafzl.confeccao.ui.componentes.EstadoVazio
import io.github.larissafzl.confeccao.ui.componentes.ItemLista
import io.github.larissafzl.confeccao.ui.componentes.TituloSecao
import io.github.larissafzl.confeccao.util.Formatos
import java.time.YearMonth

@Composable
fun TelaReciclagem(
    aoRegistrar: () -> Unit,
    viewModel: ReciclagemViewModel = viewModel()
) {
    val resumo by viewModel.resumo.collectAsStateWithLifecycle()
    var paraExcluir by remember { mutableStateOf<Reciclagem?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            BarraSuperior("Reciclagem")
            val dados = resumo
            if (dados == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                return@Column
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PainelMes(dados, viewModel::mesAnterior, viewModel::proximoMes) }
                item { TituloSecao("Registros do mês") }
                if (dados.registros.isEmpty()) {
                    item {
                        EstadoVazio(
                            "Nenhum registro neste mês",
                            "Toque em Registrar quando vender os resíduos para a recicladora."
                        )
                    }
                } else {
                    items(dados.registros, key = { it.id }) { registro ->
                        ItemLista(
                            titulo = registro.material.rotulo,
                            subtitulo = listOf(Formatos.data(registro.data), registro.comprador)
                                .filter { it.isNotBlank() }.joinToString(" · "),
                            iniciais = siglaDoMaterial(registro.material),
                            valor = "${Formatos.numero(registro.quantidade)} ${registro.unidade}",
                            aoClicar = { paraExcluir = registro },
                            mostrarSeta = false,
                            fundoIniciais = MaterialTheme.colorScheme.secondaryContainer,
                            corIniciais = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        BotaoFlutuante(
            "Registrar",
            aoRegistrar,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    paraExcluir?.let { registro ->
        AlertDialog(
            onDismissRequest = { paraExcluir = null },
            title = { Text(registro.material.rotulo) },
            text = {
                Text(
                    "${Formatos.data(registro.data)} · ${Formatos.numero(registro.quantidade)} ${registro.unidade} · " +
                        Formatos.moeda(registro.valorCentavos)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluir(registro.id)
                    paraExcluir = null
                }) { Text("Excluir registro") }
            },
            dismissButton = {
                TextButton(onClick = { paraExcluir = null }) { Text("Fechar") }
            }
        )
    }
}

@Composable
private fun PainelMes(resumo: ResumoMes, aoAnterior: () -> Unit, aoProximo: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.ic_folha),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                Formatos.mesAno(resumo.mes),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
            IconButton(onClick = aoAnterior) {
                Icon(painterResource(R.drawable.ic_voltar), contentDescription = "Mês anterior", tint = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = aoProximo, enabled = resumo.mes < YearMonth.now()) {
                Icon(painterResource(R.drawable.ic_seta_direita), contentDescription = "Próximo mês", tint = MaterialTheme.colorScheme.secondary)
            }
        }
        Text("${Formatos.numero(resumo.quilos)} kg reciclados", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            "${Formatos.moeda(resumo.valorCentavos)} recebidos com a venda dos resíduos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun siglaDoMaterial(material: Material) = when (material) {
    Material.RETALHOS -> "TP"
    Material.TUBOS -> "TB"
    Material.EMBALAGENS -> "EM"
    Material.LATAS -> "LT"
    Material.OUTRO -> "OU"
}
