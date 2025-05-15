package com.hitwaves.api

data class ApiResult<T>(
    val success: Boolean,
    val data: T? = null,
    val errorMessage: String? = null
)