package io.github.larissafzl.confeccao.ui.telas.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.ui.componentes.BotaoPrimario
import io.github.larissafzl.confeccao.ui.componentes.CampoTexto
import io.github.larissafzl.confeccao.ui.componentes.Logo

@Composable
fun TelaLogin(aoEntrar: () -> Unit, viewModel: LoginViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.slogan),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))

        CampoTexto(
            rotulo = "E-mail",
            valor = viewModel.email,
            aoMudar = { viewModel.email = it },
            modifier = Modifier.fillMaxWidth(),
            dica = "seu@email.com",
            tipoTeclado = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        CampoTexto(
            rotulo = "Senha",
            valor = viewModel.senha,
            aoMudar = { viewModel.senha = it },
            modifier = Modifier.fillMaxWidth(),
            senha = true
        )

        viewModel.erro?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        BotaoPrimario(
            texto = if (viewModel.carregando) "Entrando..." else "Entrar",
            aoClicar = { viewModel.entrar(aoEntrar) },
            modifier = Modifier.fillMaxWidth(),
            habilitado = !viewModel.carregando
        )
    }
}
