package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.*

@Composable
fun CustomMessageBox(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = Typography.titleLarge.copy(
                    color = Secondary,
                    fontSize = 24.sp
                )
            )
        },
        text = {
            Text(
                text = message,
                style = Typography.bodyLarge.copy(
                    color = Secondary,
                    fontSize = 18.sp
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier
                    .background(Secondary, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Ok",
                    style = Typography.labelSmall.copy(color = Primary),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = Typography.labelSmall.copy(color = Secondary),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        containerColor = Primary,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    )
}