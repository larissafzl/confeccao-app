package io.github.larissafzl.confeccao.dados

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate

class PedidoRepositorio(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val colecao get() = db.collection("pedidos")

    fun observarTodos(): Flow<List<Pedido>> = observarLista(
        colecao.orderBy("numero", Query.Direction.DESCENDING)
    )

    fun observarDoCliente(clienteId: String): Flow<List<Pedido>> = observarLista(
        colecao.whereEqualTo("clienteId", clienteId)
    ) { lista -> lista.sortedByDescending { it.numero } }

    fun observar(id: String): Flow<Pedido?> = callbackFlow {
        val registro = colecao.document(id).addSnapshotListener { documento, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(documento?.takeIf { it.exists() }?.paraPedido())
        }
        awaitClose { registro.remove() }
    }

    fun salvar(pedido: Pedido): String {
        val documento = if (pedido.id.isBlank()) colecao.document() else colecao.document(pedido.id)
        documento.set(pedido.paraMapa())
        return documento.id
    }

    fun excluir(id: String) {
        colecao.document(id).delete()
    }

    private fun observarLista(
        consulta: Query,
        ordenar: (List<Pedido>) -> List<Pedido> = { it }
    ): Flow<List<Pedido>> = callbackFlow {
        val registro = consulta.addSnapshotListener { resultado, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(ordenar(resultado?.documents?.map { it.paraPedido() }.orEmpty()))
        }
        awaitClose { registro.remove() }
    }

    private fun DocumentSnapshot.paraPedido(): Pedido {
        @Suppress("UNCHECKED_CAST")
        val gravacao = get("gravacao") as? Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val despacho = get("despacho") as? Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val parcelas = get("parcelas") as? List<Map<String, Any?>>
        @Suppress("UNCHECKED_CAST")
        val prazos = get("prazosDias") as? List<Number>

        return Pedido(
            id = id,
            numero = getLong("numero")?.toInt() ?: 0,
            clienteId = getString("clienteId").orEmpty(),
            clienteNome = getString("clienteNome").orEmpty(),
            modeloId = getString("modeloId").orEmpty(),
            modeloNome = getString("modeloNome").orEmpty(),
            quantidade = getLong("quantidade")?.toInt() ?: 0,
            precoUnitarioCentavos = getLong("precoUnitarioCentavos") ?: 0,
            gravacao = gravacao?.let {
                Gravacao(
                    texto = it["texto"] as? String ?: "",
                    tecnica = it["tecnica"] as? String ?: "",
                    posicao = it["posicao"] as? String ?: ""
                )
            },
            cor = getString("cor").orEmpty(),
            prazosDias = prazos?.map { it.toInt() } ?: Pedido.PRAZOS_PADRAO,
            frete = enumOuPadrao(getString("frete"), Frete.CLIENTE),
            previsaoDespacho = data(getString("previsaoDespacho")),
            observacoes = getString("observacoes").orEmpty(),
            criadoEm = data(getString("criadoEm")) ?: LocalDate.now(),
            status = enumOuPadrao(getString("status"), StatusPedido.EM_PRODUCAO),
            despacho = despacho?.let {
                Despacho(
                    data = data(it["data"] as? String) ?: LocalDate.now(),
                    volumes = (it["volumes"] as? Number)?.toInt() ?: 0,
                    pesoKg = (it["pesoKg"] as? Number)?.toDouble() ?: 0.0,
                    medidasCaixa = it["medidasCaixa"] as? String ?: "",
                    transportadora = it["transportadora"] as? String ?: "",
                    valorFreteCentavos = (it["valorFreteCentavos"] as? Number)?.toLong() ?: 0,
                    notaFiscal = it["notaFiscal"] as? String ?: ""
                )
            },
            parcelas = parcelas.orEmpty().map {
                Parcela(
                    numero = (it["numero"] as? Number)?.toInt() ?: 1,
                    vencimento = data(it["vencimento"] as? String) ?: LocalDate.now(),
                    valorCentavos = (it["valorCentavos"] as? Number)?.toLong() ?: 0,
                    paga = it["paga"] as? Boolean ?: false
                )
            }
        )
    }

    private fun Pedido.paraMapa(): Map<String, Any?> = mapOf(
        "numero" to numero,
        "clienteId" to clienteId,
        "clienteNome" to clienteNome,
        "modeloId" to modeloId,
        "modeloNome" to modeloNome,
        "quantidade" to quantidade,
        "precoUnitarioCentavos" to precoUnitarioCentavos,
        "gravacao" to gravacao?.let {
            mapOf("texto" to it.texto, "tecnica" to it.tecnica, "posicao" to it.posicao)
        },
        "cor" to cor,
        "prazosDias" to prazosDias,
        "frete" to frete.name,
        "previsaoDespacho" to previsaoDespacho?.toString(),
        "observacoes" to observacoes,
        "criadoEm" to criadoEm.toString(),
        "status" to status.name,
        "despacho" to despacho?.let {
            mapOf(
                "data" to it.data.toString(),
                "volumes" to it.volumes,
                "pesoKg" to it.pesoKg,
                "medidasCaixa" to it.medidasCaixa,
                "transportadora" to it.transportadora,
                "valorFreteCentavos" to it.valorFreteCentavos,
                "notaFiscal" to it.notaFiscal
            )
        },
        "parcelas" to parcelas.map {
            mapOf(
                "numero" to it.numero,
                "vencimento" to it.vencimento.toString(),
                "valorCentavos" to it.valorCentavos,
                "paga" to it.paga
            )
        }
    )

    private fun data(texto: String?): LocalDate? = texto?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    private inline fun <reified T : Enum<T>> enumOuPadrao(nome: String?, padrao: T): T =
        enumValues<T>().firstOrNull { it.name == nome } ?: padrao
}
