package com.example.technical_test_indomarco.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.technical_test_indomarco.di.AuthModule

@Composable
fun RequireAuth(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isLoggedIn = remember { AuthModule.provideSecureTokenManager(context).isLoggedIn() }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(AppRoutes.LOGIN.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (isLoggedIn) {
        content()
    }
}
