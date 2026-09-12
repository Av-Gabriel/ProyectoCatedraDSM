package com.udb.appfinanzas.movimientos.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

fun mapearIcono(nombreIcono: String?): ImageVector {
    return when (nombreIcono?.lowercase()) {
        "shopping_bag" -> Icons.Default.ShoppingBag
        "work" -> Icons.Default.Work
        "tv" -> Icons.Default.Tv
        "swap_horiz" -> Icons.Default.SwapHoriz
        "restaurant" -> Icons.Default.Restaurant
        else -> Icons.Default.AttachMoney
    }
}