package io.github.larissafzl.confeccao.ui.telas.reciclagem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Material
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.CampoData
import io.github.larissafzl.confeccao.ui.componentes.CampoSelecao
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto

@Composable
fun TelaRegistrarReciclagem(
    aoVoltar: () -> Unit,
    viewModel: RegistrarReciclagemViewModel = viewModel()
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior("Registrar reciclagem", aoVoltar = aoVoltar)

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoData("Data", viewModel.data, { viewModel.data = it }, Modifier.fillMaxWidth())

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Material",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Material.entries.forEach { material ->
                        ChipMaterial(material, material == viewModel.material) { viewModel.material = material }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    "Quantidade", viewModel.quantidadeTexto, { viewModel.quantidadeTexto = it },
                    Modifier.weight(1f), tipoTeclado = KeyboardType.Decimal, erro = viewModel.erroQuantidade
                )
                CampoSelecao(
                    "Unidade", viewModel.unidades, viewModel.unidade,
                    { viewModel.unidade = it }, Modifier.weight(1f)
                )
            }
            CampoTexto(
                "Comprador", viewModel.comprador, { viewModel.comprador = it },
                Modifier.fillMaxWidth(), dica = "Empresa de reciclagem"
            )
            CampoTexto(
                "Valor recebido", viewModel.valorTexto, { viewModel.valorTexto = it },
                Modifier.fillMaxWidth(), dica = "0,00", tipoTeclado = KeyboardType.Decimal,
                erro = viewModel.erroValor
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_folha),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Cada registro entra no painel do mês e mostra quanto a reciclagem devolve para a empresa.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BotaoPrimario(
                "Salvar registro",
                { viewModel.salvar(aoVoltar) },
                Modifier.fillMaxWidth(),
                icone = R.drawable.ic_check
            )
        }
    }
}

@Composable
private fun ChipMaterial(material: Material, selecionado: Boolean, aoSelecionar: () -> Unit) {
    FilterChip(
        selected = selecionado,
        onClick = aoSelecionar,
        label = { Text(material.rotulo) },
        leadingIcon = if (selecionado) {
            { Icon(painterResource(R.drawable.ic_check), contentDescription = null, Modifier.size(16.dp)) }
        } else null,
        shape = RoundedCornerShape(18.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.secondary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.secondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selecionado,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.secondary
        )
    )
}
