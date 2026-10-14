package com.example.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Map : Screen(
        route = "mapa",
        title = "Mapa",
        icon = Icons.Outlined.LocationOn,
        selectedIcon = Icons.Filled.LocationOn
    )

    object Community : Screen(
        route = "comunidad",
        title = "Comunidad",
        icon = Icons.Outlined.People,
        selectedIcon = Icons.Filled.People
    )

    object Auth : Screen(
        route = "registro",
        title = "Registro",
        icon = Icons.Outlined.PersonAdd,
        selectedIcon = Icons.Filled.PersonAdd
    )

    object About : Screen(
        route = "nosotros",
        title = "Nosotros",
        icon = Icons.Outlined.Info,
        selectedIcon = Icons.Filled.Info
    )

    companion object {
        val items = listOf(Map, Community, Auth, About)
    }
}
