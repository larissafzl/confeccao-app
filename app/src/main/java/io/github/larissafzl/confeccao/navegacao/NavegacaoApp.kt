package io.github.larissafzl.confeccao.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.larissafzl.confeccao.ui.telas.TelaProvisoria

@Composable
fun NavegacaoApp(navController: NavHostController = rememberNavController()) {
    val voltar: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Login) {
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
                listOf(
                    "Modelos" to { navController.navigate(Modelos) },
                    "Novo pedido" to { navController.navigate(NovoPedido()) },
                    "Pedidos" to { navController.navigate(Pedidos) },
                    "Clientes" to { navController.navigate(Clientes) },
                    "Reciclagem" to { navController.navigate(Reciclagem) }
                )
            )
        }

        composable<Modelos> {
            TelaProvisoria(
                "Modelos",
                listOf(
                    "Novo modelo" to { navController.navigate(EditarModelo()) },
                    "Voltar" to voltar
                )
            )
        }

        composable<EditarModelo> { entrada ->
            val rota = entrada.toRoute<EditarModelo>()
            val titulo = if (rota.modeloId == null) "Novo modelo" else "Editar modelo"
            TelaProvisoria(titulo, listOf("Voltar" to voltar))
        }

        composable<Pedidos> {
            TelaProvisoria(
                "Pedidos",
                listOf(
                    "Novo pedido" to { navController.navigate(NovoPedido()) },
                    "Ver um pedido" to { navController.navigate(DetalhePedido("exemplo")) },
                    "Voltar" to voltar
                )
            )
        }

        composable<NovoPedido> {
            TelaProvisoria("Novo pedido", listOf("Voltar" to voltar))
        }

        composable<DetalhePedido> { entrada ->
            val rota = entrada.toRoute<DetalhePedido>()
            TelaProvisoria(
                "Detalhe do pedido",
                listOf(
                    "Marcar como despachado" to { navController.navigate(Despachar(rota.pedidoId)) },
                    "Voltar" to voltar
                )
            )
        }

        composable<Despachar> {
            TelaProvisoria("Despachar pedido", listOf("Voltar" to voltar))
        }

        composable<Clientes> {
            TelaProvisoria(
                "Clientes",
                listOf(
                    "Ver um cliente" to { navController.navigate(DetalheCliente("exemplo")) },
                    "Voltar" to voltar
                )
            )
        }

        composable<DetalheCliente> { entrada ->
            val rota = entrada.toRoute<DetalheCliente>()
            TelaProvisoria(
                "Detalhe do cliente",
                listOf(
                    "Novo pedido para este cliente" to {
                        navController.navigate(NovoPedido(clienteId = rota.clienteId))
                    },
                    "Voltar" to voltar
                )
            )
        }

        composable<Reciclagem> {
            TelaProvisoria(
                "Reciclagem",
                listOf(
                    "Registrar venda" to { navController.navigate(RegistrarReciclagem) },
                    "Voltar" to voltar
                )
            )
        }

        composable<RegistrarReciclagem> {
            TelaProvisoria("Registrar reciclagem", listOf("Voltar" to voltar))
        }
    }
}
