package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography

@Composable
fun CustomSnackbar(snackbarHostState: SnackbarHostState) {

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 40.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData: SnackbarData ->
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(color = Primary, shape = RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = snackbarData.visuals.message,
                        style = Typography.labelSmall.copy(
                            fontSize = 18.sp,
                            color = Secondary
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        )
    }


}