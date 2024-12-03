package com.example.hacaton

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.hacaton.API.ApiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setContent {
            val apiState by viewModel.apiState.collectAsState()
            var showOfflineButton by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(5000)
                showOfflineButton = true
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Добавляем логотип
                    Image(
                        painter = painterResource(
                            id = if (isSystemInDarkTheme()) {
                                R.drawable.logo
                            } else {
                                R.drawable.logo

                            }
                        ),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 32.dp)
                    )

                    when (apiState) {
                        is ApiState.Loading -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Подключение к серверу...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (showOfflineButton) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.switchToOfflineMode() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Перейти в автономный режим")
                                }
                            }
                        }

                        is ApiState.Success -> {
                            LaunchedEffect(Unit) {
                                navigateToNextScreen()
                            }
                        }

                        is ApiState.Error -> {
                            LaunchedEffect(Unit) {
                                navigateToNextScreen(true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToNextScreen(offlineMode: Boolean = false) {
        val isTeacher = sharedPreferences.getInt("isTeacher", -1)
        val selectedItem = sharedPreferences.getString("selectedItem", null)

        if (isTeacher == -1 || selectedItem == null) {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("offlineMode", offlineMode)
            })
        } else {
            startActivity(Intent(this, MainActivityRaspis::class.java).apply {
                putExtra("isTeacher", isTeacher)
                putExtra("selectedItem", selectedItem)
                putExtra("offlineMode", offlineMode)
            })
        }
        finish()
    }
}