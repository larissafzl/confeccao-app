package io.github.larissafzl.confeccao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.larissafzl.confeccao.navegacao.NavegacaoApp
import io.github.larissafzl.confeccao.ui.theme.ConfeccaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConfeccaoTheme {
                NavegacaoApp()
            }
        }
    }
}
