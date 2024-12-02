package com.example.hacaton.API

sealed class ApiState {
    object Loading : ApiState()
    object Success : ApiState()
    data class Error(val message: String) : ApiState()
}