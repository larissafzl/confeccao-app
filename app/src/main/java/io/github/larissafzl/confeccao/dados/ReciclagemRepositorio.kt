package io.github.larissafzl.confeccao.dados

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate

class ReciclagemRepositorio(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val colecao get() = db.collection("reciclagem")

    fun observarTodos(): Flow<List<Reciclagem>> = callbackFlow {
        val registro = colecao.orderBy("data", Query.Direction.DESCENDING).addSnapshotListener { resultado, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(resultado?.documents?.map { it.paraReciclagem() }.orEmpty())
        }
        awaitClose { registro.remove() }
    }

    fun salvar(reciclagem: Reciclagem): String {
        val documento = if (reciclagem.id.isBlank()) colecao.document() else colecao.document(reciclagem.id)
        documento.set(reciclagem.paraMapa())
        return documento.id
    }

    fun excluir(id: String) {
        colecao.document(id).delete()
    }

    private fun DocumentSnapshot.paraReciclagem() = Reciclagem(
        id = id,
        data = getString("data")?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now(),
        material = Material.entries.firstOrNull { it.name == getString("material") } ?: Material.OUTRO,
        quantidade = getDouble("quantidade") ?: 0.0,
        unidade = getString("unidade") ?: "kg",
        comprador = getString("comprador").orEmpty(),
        valorCentavos = getLong("valorCentavos") ?: 0
    )

    private fun Reciclagem.paraMapa() = mapOf(
        "data" to data.toString(),
        "material" to material.name,
        "quantidade" to quantidade,
        "unidade" to unidade,
        "comprador" to comprador,
        "valorCentavos" to valorCentavos
    )
}
