package com.hitwaves.api

data class ApiResult<T>(
    var success: Boolean,
    val data: T? = null,
    val errorMessage: String? = null
)