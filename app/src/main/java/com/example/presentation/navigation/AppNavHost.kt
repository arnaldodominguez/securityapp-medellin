package com.example.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.MapRepositoryImpl
import com.example.data.repository.ReportsRepositoryImpl
import com.example.presentation.about.AboutScreen
import com.example.presentation.auth.AuthScreen
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.community.CommunityScreen
import com.example.presentation.community.CommunityViewModel
import com.example.presentation.map.MapScreen
import com.example.presentation.map.MapViewModel
import com.example.ui.theme.FigmaBackground
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.TacticalBackground
import com.example.ui.theme.TacticalOnSurface
import com.example.ui.theme.TacticalOnSurfaceVariant
import com.example.ui.theme.TacticalPrimary
import com.example.ui.theme.TacticalPrimaryContainer
import com.example.ui.theme.TacticalSurfaceCard

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val mapRepo = remember { MapRepositoryImpl() }
    val reportsRepo = remember { ReportsRepositoryImpl() }
    val authRepo = remember { AuthRepositoryImpl() }

    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val gpsTracker = remember { com.example.core.location.GpsLocationTracker(context) }
    val bleManager = remember { com.example.core.bluetooth.Esp32BleManager(context) }

    val mapViewModel = remember {
        MapViewModel(
            mapRepository = mapRepo,
            authRepository = authRepo,
            gpsTracker = gpsTracker,
            bleManager = bleManager
        )
    }
    val communityViewModel = remember { CommunityViewModel(reportsRepo) }
    val authViewModel = remember { AuthViewModel(authRepo) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FigmaBackground,
        bottomBar = {
            NavigationBar(
                containerColor = FigmaSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
            ) {
                Screen.items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FigmaTealPrimary,
                            selectedTextColor = FigmaTealPrimary,
                            unselectedIconColor = Color(0xFF78928E),
                            unselectedTextColor = Color(0xFF78928E),
                            indicatorColor = Color(0xFFE2EFEF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Map.route) {
                MapScreen(
                    viewModel = mapViewModel,
                    onNavigateToProfile = {
                        navController.navigate(Screen.Auth.route)
                    },
                    onNavigateToReport = {
                        navController.navigate(Screen.Community.route)
                        communityViewModel.openReportModal()
                    }
                )
            }

            composable(Screen.Community.route) {
                CommunityScreen(
                    viewModel = communityViewModel,
                    onNavigateToProfile = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
        }
    }
}
