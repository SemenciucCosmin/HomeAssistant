package com.example.homeassistant.domain.api

import java.lang.Exception

sealed class CallResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : CallResult<T>()
    data class Error(val exception: Exception) : CallResult<Nothing>()
}
