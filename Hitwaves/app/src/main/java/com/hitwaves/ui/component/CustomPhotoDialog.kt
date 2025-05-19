package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography

@Composable
fun CustomPhotoDialog(
    title: String,
    message: String,
    onConfirmGallery: () -> Unit,
    onConfirmCamera: () -> Unit,
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
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column (
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    style = Typography.bodyLarge.copy(
                        color = Secondary,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 2.dp)
                        .background(Primary)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onConfirmGallery,
                        modifier = Modifier
                            .background(Secondary, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            "Gallery",
                            style = Typography.labelSmall.copy(color = Primary),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    TextButton(
                        onClick = onConfirmCamera,
                        modifier = Modifier
                            .background(Secondary, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            "Camera",
                            style = Typography.labelSmall.copy(color = Primary),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        containerColor = Primary,
        shape = RoundedCornerShape(16.dp),
        confirmButton = {},
        dismissButton = {}
    )
}