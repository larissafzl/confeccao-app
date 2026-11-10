package io.github.larissafzl.confeccao.navegacao

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.R
import io.github.larissafzl.confeccao.dados.Autenticacao
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.MenuOpcoes
import io.github.larissafzl.confeccao.ui.telas.TelaProvisoria
import io.github.larissafzl.confeccao.ui.telas.clientes.TelaClientes
import io.github.larissafzl.confeccao.ui.telas.clientes.TelaDetalheCliente
import io.github.larissafzl.confeccao.ui.telas.clientes.TelaEditarCliente
import io.github.larissafzl.confeccao.ui.telas.login.TelaLogin
import io.github.larissafzl.confeccao.ui.telas.modelos.TelaEditarModelo
import io.github.larissafzl.confeccao.ui.telas.modelos.TelaModelos
import io.github.larissafzl.confeccao.ui.telas.pedidos.TelaDespachar
import io.github.larissafzl.confeccao.ui.telas.pedidos.TelaDetalhePedido
import io.github.larissafzl.confeccao.ui.telas.pedidos.TelaNovoPedido
import io.github.larissafzl.confeccao.ui.telas.pedidos.TelaPedidos
import io.github.larissafzl.confeccao.ui.telas.reciclagem.TelaReciclagem
import io.github.larissafzl.confeccao.ui.telas.reciclagem.TelaRegistrarReciclagem

@Composable
fun NavegacaoApp(navController: NavHostController = rememberNavController()) {
    val voltar: () -> Unit = { navController.popBackStack() }
    val destinoInicial: Any = remember { if (Autenticacao.logado) Inicio else Login }
    val entradaAtual by navController.currentBackStackEntryAsState()
    val abaAtual = Aba.entries.find { aba ->
        entradaAtual?.destination?.hasRoute(aba.rota::class) == true
    }

    Scaffold(
        bottomBar = {
            if (abaAtual != null) {
                BarraNavegacao(abaAtual) { aba -> irParaAba(navController, aba) }
            }
        }
    ) { espacamento ->
        NavHost(
            navController = navController,
            startDestination = destinoInicial,
            modifier = Modifier
                .padding(espacamento)
                .consumeWindowInsets(espacamento)
        ) {
            composable<Login> {
                TelaLogin(aoEntrar = {
                    navController.navigate(Inicio) {
                        popUpTo(Login) { inclusive = true }
                    }
                })
            }

            composable<Inicio> {
                TelaProvisoria(
                    "Início",
                    listOf("Novo pedido" to { navController.navigate(NovoPedido()) }),
                    barraSuperior = {
                        BarraSuperior(
                            stringResource(R.string.app_name),
                            acoes = {
                                MenuOpcoes(
                                    listOf(
                                        "Modelos de capa" to { navController.navigate(Modelos) },
                                        "Sair" to {
                                            Autenticacao.sair()
                                            navController.navigate(Login) {
                                                popUpTo(navController.graph.id) { inclusive = true }
                                            }
                                        }
                                    )
                                )
                            }
                        )
                    }
                )
            }

            composable<Modelos> {
                TelaModelos(
                    aoVoltar = voltar,
                    aoAbrirModelo = { navController.navigate(EditarModelo(it)) },
                    aoNovoModelo = { navController.navigate(EditarModelo()) }
                )
            }

            composable<EditarModelo> {
                TelaEditarModelo(aoVoltar = voltar)
            }

            composable<Pedidos> {
                TelaPedidos(
                    aoAbrirPedido = { navController.navigate(DetalhePedido(it)) },
                    aoNovoPedido = { navController.navigate(NovoPedido()) }
                )
            }

            composable<NovoPedido> {
                TelaNovoPedido(
                    aoVoltar = voltar,
                    aoSalvar = { id ->
                        navController.navigate(DetalhePedido(id)) {
                            popUpTo<NovoPedido> { inclusive = true }
                        }
                    }
                )
            }

            composable<DetalhePedido> {
                TelaDetalhePedido(
                    aoVoltar = voltar,
                    aoDespachar = { navController.navigate(Despachar(it)) }
                )
            }

            composable<Despachar> {
                TelaDespachar(aoVoltar = voltar)
            }

            composable<Clientes> {
                TelaClientes(
                    aoAbrirCliente = { navController.navigate(DetalheCliente(it)) },
                    aoNovoCliente = { navController.navigate(EditarCliente()) }
                )
            }

            composable<EditarCliente> {
                TelaEditarCliente(
                    aoVoltar = voltar,
                    aoExcluir = { navController.popBackStack(Clientes, inclusive = false) }
                )
            }

            composable<DetalheCliente> {
                TelaDetalheCliente(
                    aoVoltar = voltar,
                    aoEditar = { navController.navigate(EditarCliente(it)) },
                    aoNovoPedido = { navController.navigate(NovoPedido(clienteId = it)) },
                    aoAbrirPedido = { navController.navigate(DetalhePedido(it)) }
                )
            }

            composable<Reciclagem> {
                TelaReciclagem(aoRegistrar = { navController.navigate(RegistrarReciclagem) })
            }

            composable<RegistrarReciclagem> {
                TelaRegistrarReciclagem(aoVoltar = voltar)
            }
        }
    }
}

private fun irParaAba(navController: NavHostController, aba: Aba) {
    navController.navigate(aba.rota) {
        popUpTo(Inicio) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
