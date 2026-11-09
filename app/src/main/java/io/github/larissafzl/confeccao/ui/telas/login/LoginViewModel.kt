package io.github.larissafzl.confeccao.ui.telas.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.github.larissafzl.confeccao.dados.Autenticacao
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var senha by mutableStateOf("")
    var carregando by mutableStateOf(false)
        private set
    var erro by mutableStateOf<String?>(null)
        private set

    fun entrar(aoEntrar: () -> Unit) {
        if (email.isBlank() || senha.isBlank()) {
            erro = "Preencha o e-mail e a senha."
            return
        }
        erro = null
        carregando = true
        viewModelScope.launch {
            Autenticacao.entrar(email.trim(), senha)
                .onSuccess { aoEntrar() }
                .onFailure { erro = mensagemDeErro(it) }
            carregando = false
        }
    }

    private fun mensagemDeErro(e: Throwable): String = when (e) {
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "E-mail ou senha incorretos."
        is FirebaseNetworkException -> "Sem conexão com a internet."
        is FirebaseTooManyRequestsException -> "Muitas tentativas. Aguarde alguns minutos."
        else -> "Não foi possível entrar. Tente novamente."
    }
}
