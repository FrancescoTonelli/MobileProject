package com.hitwaves.ui.component

import android.icu.util.Calendar
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hitwaves.R
import com.hitwaves.ui.theme.*
import java.util.Locale

@Composable
fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(
            label,
            color = Secondary,
            style = Typography.labelSmall
        ) },
        modifier = modifier.width(300.dp).height(60.dp),
        singleLine = true,
        textStyle = Typography.bodyLarge.copy(color = Secondary),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Secondary,
            unfocusedTextColor = Secondary,
            focusedContainerColor = FgDark,
            unfocusedContainerColor = FgDark,
            focusedBorderColor = Primary,
            unfocusedBorderColor = Secondary,
            cursorColor = Primary
        ),
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
fun LoginPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    LoginInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isPassword = !passwordVisible,
        modifier = modifier,
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = if (passwordVisible) R.drawable.eye else R.drawable.eye_slash),
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
fun LoginDateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.ITALY, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onValueChange(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LoginInputField(
        value = value,
        onValueChange = {},
        label = label,
        modifier = modifier,
        trailingIcon = {
            IconButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Select birthdate",
                    tint = Secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}