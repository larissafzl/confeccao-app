package io.github.larissafzl.confeccao.navegacao

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import io.github.larissafzl.confeccao.ui.componentes.BarraSuperior
import io.github.larissafzl.confeccao.ui.componentes.MenuOpcoes
import io.github.larissafzl.confeccao.ui.telas.TelaProvisoria

@Composable
fun NavegacaoApp(navController: NavHostController = rememberNavController()) {
    val voltar: () -> Unit = { navController.popBackStack() }
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
            startDestination = Login,
            modifier = Modifier
                .padding(espacamento)
                .consumeWindowInsets(espacamento)
        ) {
            composable<Login> {
                TelaProvisoria(
                    "Login",
                    listOf("Entrar" to {
                        navController.navigate(Inicio) {
                            popUpTo(Login) { inclusive = true }
                        }
                    })
                )
            }

            composable<Inicio> {
                TelaProvisoria(
                    "Início",
                    listOf("Novo pedido" to { navController.navigate(NovoPedido()) }),
                    barraSuperior = {
                        BarraSuperior(
                            stringResource(R.string.app_name),
                            acoes = {
                                MenuOpcoes(listOf("Modelos de capa" to { navController.navigate(Modelos) }))
                            }
                        )
                    }
                )
            }

            composable<Modelos> {
                TelaProvisoria(
                    "Modelos",
                    listOf("Novo modelo" to { navController.navigate(EditarModelo()) }),
                    barraSuperior = { BarraSuperior("Modelos", aoVoltar = voltar) }
                )
            }

            composable<EditarModelo> { entrada ->
                val rota = entrada.toRoute<EditarModelo>()
                val titulo = if (rota.modeloId == null) "Novo modelo" else "Editar modelo"
                TelaProvisoria(titulo, barraSuperior = { BarraSuperior(titulo, aoVoltar = voltar) })
            }

            composable<Pedidos> {
                TelaProvisoria(
                    "Pedidos",
                    listOf(
                        "Novo pedido" to { navController.navigate(NovoPedido()) },
                        "Ver um pedido" to { navController.navigate(DetalhePedido("exemplo")) }
                    ),
                    barraSuperior = { BarraSuperior("Pedidos") }
                )
            }

            composable<NovoPedido> {
                TelaProvisoria(
                    "Novo pedido",
                    barraSuperior = { BarraSuperior("Novo pedido", aoVoltar = voltar) }
                )
            }

            composable<DetalhePedido> { entrada ->
                val rota = entrada.toRoute<DetalhePedido>()
                TelaProvisoria(
                    "Pedido",
                    listOf(
                        "Marcar como despachado" to { navController.navigate(Despachar(rota.pedidoId)) }
                    ),
                    barraSuperior = { BarraSuperior("Pedido", aoVoltar = voltar) }
                )
            }

            composable<Despachar> {
                TelaProvisoria(
                    "Despachar pedido",
                    barraSuperior = { BarraSuperior("Despachar pedido", aoVoltar = voltar) }
                )
            }

            composable<Clientes> {
                TelaProvisoria(
                    "Clientes",
                    listOf("Ver um cliente" to { navController.navigate(DetalheCliente("exemplo")) }),
                    barraSuperior = { BarraSuperior("Clientes") }
                )
            }

            composable<DetalheCliente> { entrada ->
                val rota = entrada.toRoute<DetalheCliente>()
                TelaProvisoria(
                    "Cliente",
                    listOf(
                        "Novo pedido para este cliente" to {
                            navController.navigate(NovoPedido(clienteId = rota.clienteId))
                        }
                    ),
                    barraSuperior = { BarraSuperior("Cliente", aoVoltar = voltar) }
                )
            }

            composable<Reciclagem> {
                TelaProvisoria(
                    "Reciclagem",
                    listOf("Registrar venda" to { navController.navigate(RegistrarReciclagem) }),
                    barraSuperior = { BarraSuperior("Reciclagem") }
                )
            }

            composable<RegistrarReciclagem> {
                TelaProvisoria(
                    "Registrar reciclagem",
                    barraSuperior = { BarraSuperior("Registrar reciclagem", aoVoltar = voltar) }
                )
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
