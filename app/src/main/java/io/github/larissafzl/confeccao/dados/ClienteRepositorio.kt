package io.github.larissafzl.confeccao.dados

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ClienteRepositorio(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val colecao get() = db.collection("clientes")

    fun observarTodos(): Flow<List<Cliente>> = callbackFlow {
        val registro = colecao.orderBy("nome").addSnapshotListener { resultado, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(resultado?.documents?.map { it.paraCliente() }.orEmpty())
        }
        awaitClose { registro.remove() }
    }

    fun observar(id: String): Flow<Cliente?> = callbackFlow {
        val registro = colecao.document(id).addSnapshotListener { documento, erro ->
            if (erro != null) {
                close(erro)
                return@addSnapshotListener
            }
            trySend(documento?.takeIf { it.exists() }?.paraCliente())
        }
        awaitClose { registro.remove() }
    }

    // Não espera a resposta do servidor, assim o app também grava sem internet
    fun salvar(cliente: Cliente): String {
        val documento = if (cliente.id.isBlank()) colecao.document() else colecao.document(cliente.id)
        documento.set(cliente.paraMapa())
        return documento.id
    }

    fun excluir(id: String) {
        colecao.document(id).delete()
    }

    private fun DocumentSnapshot.paraCliente() = Cliente(
        id = id,
        nome = getString("nome").orEmpty(),
        cnpj = getString("cnpj").orEmpty(),
        inscricaoEstadual = getString("inscricaoEstadual").orEmpty(),
        endereco = getString("endereco").orEmpty(),
        cidade = getString("cidade").orEmpty(),
        uf = getString("uf").orEmpty(),
        telefone = getString("telefone").orEmpty(),
        email = getString("email").orEmpty()
    )

    private fun Cliente.paraMapa() = mapOf(
        "nome" to nome,
        "cnpj" to cnpj,
        "inscricaoEstadual" to inscricaoEstadual,
        "endereco" to endereco,
        "cidade" to cidade,
        "uf" to uf,
        "telefone" to telefone,
        "email" to email
    )
}
