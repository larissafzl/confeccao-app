package io.github.larissafzl.confeccao.ui.telas.modelos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Calculos
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.BotaoSecundario
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto
import io.github.larissafzl.confeccao.util.Formatos

@Composable
fun TelaEditarModelo(
    aoVoltar: () -> Unit,
    viewModel: EditarModeloViewModel = viewModel()
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    val titulo = when {
        !viewModel.editando -> "Novo modelo"
        viewModel.nome.isNotBlank() -> viewModel.nome
        else -> "Editar modelo"
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior(titulo, aoVoltar = aoVoltar)

        if (viewModel.carregando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoTexto(
                "Nome", viewModel.nome, { viewModel.nome = it },
                Modifier.fillMaxWidth(), dica = "Ex.: Mod. 05", erro = viewModel.erroNome
            )
            CampoTexto(
                "Descrição", viewModel.descricao, { viewModel.descricao = it },
                Modifier.fillMaxWidth(), dica = "Materiais da capa"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    "Custo por capa", viewModel.custoTexto, { viewModel.custoTexto = it },
                    Modifier.weight(1f), dica = "0,00", tipoTeclado = KeyboardType.Decimal,
                    erro = viewModel.erroCusto
                )
                CampoTexto(
                    "Preço de atacado", viewModel.precoTexto, { viewModel.precoTexto = it },
                    Modifier.weight(1f), dica = "0,00", tipoTeclado = KeyboardType.Decimal,
                    erro = viewModel.erroPreco
                )
            }

            PainelLucro(viewModel.custo, viewModel.preco)

            BotaoPrimario(
                "Salvar modelo",
                { viewModel.salvar(aoVoltar) },
                Modifier.fillMaxWidth(),
                icone = R.drawable.ic_check
            )
            if (viewModel.editando) {
                BotaoSecundario("Excluir modelo", { confirmarExclusao = true }, Modifier.fillMaxWidth())
            }
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir modelo?") },
            text = { Text("Os pedidos já feitos com este modelo continuam salvos.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    viewModel.excluir(aoVoltar)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PainelLucro(custo: Long?, preco: Long?) {
    val precoLiquido = preco?.let { Calculos.precoSemImposto(it) }
    val lucro = if (precoLiquido != null && custo != null) precoLiquido - custo else null

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinhaCalculo("Preço após imposto (8%)", precoLiquido?.let { Formatos.moeda(it) } ?: "—")
            LinhaCalculo("Custo por capa", custo?.let { "− " + Formatos.moeda(it) } ?: "—")
            LinhaCalculo(
                "Lucro por capa",
                lucro?.let { Formatos.moeda(it) } ?: "—",
                destaque = true,
                cor = if (lucro != null && lucro < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        Text(
            "Calculado automaticamente a partir do custo e do preço.",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LinhaCalculo(
    rotulo: String,
    valor: String,
    destaque: Boolean = false,
    cor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            rotulo,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            fontWeight = if (destaque) FontWeight.SemiBold else FontWeight.Normal,
            color = if (destaque) cor else MaterialTheme.colorScheme.onSurface
        )
        Text(
            valor,
            fontSize = if (destaque) 18.sp else 14.sp,
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Medium,
            color = if (destaque) cor else MaterialTheme.colorScheme.onSurface
        )
    }
}
