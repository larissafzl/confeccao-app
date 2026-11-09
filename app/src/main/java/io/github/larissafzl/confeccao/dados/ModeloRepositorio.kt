package io.github.larissafzl.confeccao.dados

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ModeloRepositorio(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val colecao get() = db.collection("modelos")

    fun observarTodos(): Flow<List<ModeloCapa>> = callbackFlow {
        val registro = colecao.orderBy("nome").addSnapshotListener { resultado, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(resultado?.documents?.map { it.paraModelo() }.orEmpty())
        }
        awaitClose { registro.remove() }
    }

    fun observar(id: String): Flow<ModeloCapa?> = callbackFlow {
        val registro = colecao.document(id).addSnapshotListener { documento, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(documento?.takeIf { it.exists() }?.paraModelo())
        }
        awaitClose { registro.remove() }
    }

    fun salvar(modelo: ModeloCapa): String {
        val documento = if (modelo.id.isBlank()) colecao.document() else colecao.document(modelo.id)
        documento.set(modelo.paraMapa())
        return documento.id
    }

    fun excluir(id: String) {
        colecao.document(id).delete()
    }

    private fun DocumentSnapshot.paraModelo() = ModeloCapa(
        id = id,
        nome = getString("nome").orEmpty(),
        descricao = getString("descricao").orEmpty(),
        custoCentavos = getLong("custoCentavos") ?: 0,
        precoCentavos = getLong("precoCentavos") ?: 0
    )

    private fun ModeloCapa.paraMapa() = mapOf(
        "nome" to nome,
        "descricao" to descricao,
        "custoCentavos" to custoCentavos,
        "precoCentavos" to precoCentavos
    )
}
