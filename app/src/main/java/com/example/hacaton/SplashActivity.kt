package com.example.hacaton

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Имитация загрузки (в будущем здесь будет подключение к API)
        lifecycleScope.launch {
            delay(1000)
            checkFirstLaunch()
        }
    }

    private fun checkFirstLaunch() {
        val isTeacher = sharedPreferences.getInt("isTeacher", -1)
        val selectedItem = sharedPreferences.getString("selectedItem", null)

        if (isTeacher == -1 || selectedItem == null) {
            // Первый запуск - переход к выбору роли
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Повторный запуск - переход сразу к расписанию
            startActivity(Intent(this, MainActivityRaspis::class.java).apply {
                putExtra("isTeacher", isTeacher)
                putExtra("selectedItem", selectedItem)
            })
        }
        finish()
    }
}

@Composable
fun SplashScreen() {
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
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}