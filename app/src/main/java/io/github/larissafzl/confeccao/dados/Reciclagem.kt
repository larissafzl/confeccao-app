package io.github.larissafzl.confeccao.dados

import java.time.LocalDate

enum class Material(val rotulo: String) {
    RETALHOS("Retalhos de TNT e PVC"),
    TUBOS("Tubos de papelão"),
    EMBALAGENS("Embalagens"),
    LATAS("Latas de tinta")
}

data class Reciclagem(
    val id: String = "",
    val data: LocalDate = LocalDate.now(),
    val material: Material = Material.RETALHOS,
    val quantidade: Double = 0.0,
    val unidade: String = "kg",
    val comprador: String = "",
    val valorCentavos: Long = 0
)
