package io.github.larissafzl.confeccao.navegacao

import kotlinx.serialization.Serializable

// Acesso
@Serializable
object Login

// Abas da barra inferior
@Serializable
object Inicio

@Serializable
object Pedidos

@Serializable
object Clientes

@Serializable
object Reciclagem

// Modelos de capa
@Serializable
object Modelos

@Serializable
data class EditarModelo(val modeloId: String? = null)

// Pedidos
@Serializable
data class NovoPedido(val clienteId: String? = null)

@Serializable
data class DetalhePedido(val pedidoId: String)

@Serializable
data class Despachar(val pedidoId: String)

// Clientes
@Serializable
data class DetalheCliente(val clienteId: String)

// Reciclagem
@Serializable
object RegistrarReciclagem
