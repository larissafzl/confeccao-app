package io.github.larissafzl.confeccao.dados

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object Autenticacao {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    val logado: Boolean
        get() = auth.currentUser != null

    suspend fun entrar(email: String, senha: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, senha).await()
        Unit
    }

    fun sair() = auth.signOut()
}
