package com.example.hacaton.model

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hacaton.API.ApiState

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _apiState = MutableStateFlow<ApiState>(ApiState.Success)
    val apiState: StateFlow<ApiState> = _apiState

    fun loadScheduleData(isTeacher: Int, selectedItem: String) {
        viewModelScope.launch {
            _apiState.value = ApiState.Loading
            try {
                _apiState.value = ApiState.Success
            } catch (e: Exception) {
                _apiState.value = ApiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
        )
    }
}
