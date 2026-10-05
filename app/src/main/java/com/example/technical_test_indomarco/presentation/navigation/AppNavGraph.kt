package com.example.technical_test_indomarco.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.technical_test_indomarco.di.AuthModule
import com.example.technical_test_indomarco.presentation.screen.add.AddEventScreen
import com.example.technical_test_indomarco.presentation.screen.detail.DetailScreen
import com.example.technical_test_indomarco.presentation.screen.home.HomeScreen
import com.example.technical_test_indomarco.presentation.screen.list.EventListScreen
import com.example.technical_test_indomarco.presentation.screen.login.LoginScreen
import com.example.technical_test_indomarco.presentation.screen.splash.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { AuthModule.provideSecureTokenManager(context) }

    var loginSuccessMessage by remember { mutableStateOf<String?>(null) }

    val performLogout: () -> Unit = {
        loginSuccessMessage = null
        tokenManager.clearSession()
        navController.navigate(AppRoutes.LOGIN.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH.route
    ) {
        composable(AppRoutes.SPLASH.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(AppRoutes.HOME.route) {
                        popUpTo(AppRoutes.SPLASH.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.LOGIN.route) {
                        popUpTo(AppRoutes.SPLASH.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.LOGIN.route) {
            LoginScreen(
                onLoginSuccess = { message ->
                    loginSuccessMessage = message
                    navController.navigate(AppRoutes.HOME.route) {
                        popUpTo(AppRoutes.LOGIN.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.HOME.route) {
            RequireAuth(navController = navController) {
                HomeScreen(
                    loginSuccessMessage = loginSuccessMessage,
                    onLoginSuccessMessageShown = { loginSuccessMessage = null },
                    onEventClick = { eventId -> navController.navigate(AppRoutes.detailRoute(eventId)) },
                    onSeeAllClick = { navController.navigate(AppRoutes.EVENT_LIST.route) },
                    onAddEventClick = { navController.navigate(AppRoutes.ADD_EVENT.route) },
                    onLogoutClick = performLogout
                )
            }
        }

        composable(
            route = AppRoutes.DETAIL.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            RequireAuth(navController = navController) {
                DetailScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(AppRoutes.EVENT_LIST.route) {
            RequireAuth(navController = navController) {
                EventListScreen(
                    onBackClick = { navController.popBackStack() },
                    onEventClick = { eventId -> navController.navigate(AppRoutes.detailRoute(eventId)) }
                )
            }
        }

        composable(AppRoutes.ADD_EVENT.route) {
            RequireAuth(navController = navController) {
                AddEventScreen(
                    onBackClick = { navController.popBackStack() },
                    onEventSaved = { navController.popBackStack() },
                    onLogoutClick = performLogout
                )
            }
        }
    }
}
