package com.hitwaves.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.AppActivity
import com.hitwaves.LoginActivity
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TokenManager
import com.hitwaves.api.TokenResponse
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.IconData
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.SplashScreenViewModel

private fun init() : SplashScreenViewModel {
    return SplashScreenViewModel()
}

@Composable
fun SplashScreen() {

    val splashViewModel = remember { init() }
    val result: ApiResult<TokenResponse> by splashViewModel.autoLoginState
    val isLoading by splashViewModel.isAutoLoading
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val token = TokenManager.getToken()
        if (token != null) {
            splashViewModel.handleSplash()
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(result) {
        if (result.success && result.data != null) {
            TokenManager.saveToken(result.data!!.token)
            val intent = Intent(context, AppActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        } else if (!result.success && result.errorMessage != null) {
            snackbarHostState.showSnackbar(result.errorMessage!!)
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val logo = IconData(
                route = "logo",
                label = "Logo",
                icon = ImageVector.vectorResource(id = R.drawable.logo)
            )

            Icon(
                imageVector = logo.icon,
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.size(28.dp))

            Text(
                text = "Hitwaves",
                style = Typography.titleLarge.copy(
                    fontSize = 48.sp,
                    color = Secondary,
                    drawStyle = Stroke(width = 4f)
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Find your perfect wave!",
                style = Typography.labelSmall.copy(
                    fontSize = 20.sp,
                    color = Secondary
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 4.dp
            )
        }
    }


    CustomSnackbar(snackbarHostState)
}
