package io.github.larissafzl.confeccao.ui.telas.clientes

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.dados.Cliente
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.BotaoSecundario
import io.github.larissafzl.confeccao.ui.componentes.CampoSelecao
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto

private val UFS = listOf(
    "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
    "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
)

@Composable
fun TelaEditarCliente(
    aoVoltar: () -> Unit,
    aoExcluir: () -> Unit,
    viewModel: EditarClienteViewModel = viewModel()
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    val cliente = viewModel.cliente
    val atualizar = { novo: Cliente -> viewModel.cliente = novo }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior(if (viewModel.editando) "Editar cliente" else "Novo cliente", aoVoltar = aoVoltar)

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
                "Nome", cliente.nome, { atualizar(cliente.copy(nome = it)) },
                Modifier.fillMaxWidth(), dica = "Razão social ou nome fantasia", erro = viewModel.erroNome
            )
            CampoTexto(
                "CNPJ", cliente.cnpj, { atualizar(cliente.copy(cnpj = it)) },
                Modifier.fillMaxWidth(), tipoTeclado = KeyboardType.Number
            )
            CampoTexto(
                "Inscrição estadual", cliente.inscricaoEstadual, { atualizar(cliente.copy(inscricaoEstadual = it)) },
                Modifier.fillMaxWidth(), tipoTeclado = KeyboardType.Number
            )
            CampoTexto(
                "Endereço", cliente.endereco, { atualizar(cliente.copy(endereco = it)) },
                Modifier.fillMaxWidth(), dica = "Rua, número e bairro"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    "Cidade", cliente.cidade, { atualizar(cliente.copy(cidade = it)) },
                    Modifier.weight(2f)
                )
                CampoSelecao(
                    "UF", UFS, cliente.uf.ifBlank { null }, { atualizar(cliente.copy(uf = it)) },
                    Modifier.weight(1f), dica = "UF"
                )
            }
            CampoTexto(
                "Telefone", cliente.telefone, { atualizar(cliente.copy(telefone = it)) },
                Modifier.fillMaxWidth(), tipoTeclado = KeyboardType.Phone
            )
            CampoTexto(
                "E-mail", cliente.email, { atualizar(cliente.copy(email = it)) },
                Modifier.fillMaxWidth(), tipoTeclado = KeyboardType.Email
            )

            BotaoPrimario("Salvar cliente", { viewModel.salvar(aoVoltar) }, Modifier.fillMaxWidth())
            if (viewModel.editando) {
                BotaoSecundario("Excluir cliente", { confirmarExclusao = true }, Modifier.fillMaxWidth())
            }
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir cliente?") },
            text = { Text("Os pedidos já feitos continuam salvos, mas o cadastro de ${cliente.nome} será apagado.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    viewModel.excluir(aoExcluir)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}
