package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.ui.theme.BgDark
import com.hitwaves.ui.theme.Primary
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GoBack(navController: NavController) {
    var isClickable by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(35.dp)
            .clip(CircleShape)
            .background(Primary)
            .clickable(enabled = isClickable) {
                isClickable = false
                navController.popBackStack()

                scope.launch {
                    delay(500)
                    isClickable = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.back),
            contentDescription = null,
            tint = BgDark
        )
    }
}