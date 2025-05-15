package com.hitwaves.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hitwaves.ui.screens.Login
import com.hitwaves.ui.screens.Register

@Composable
fun LoginNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            Login(navController)
        }
        composable("register") {
            Register(navController)
        }
    }
}