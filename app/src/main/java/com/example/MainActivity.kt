package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.BilindiWallRepository
import com.example.ui.BilindiWallApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val isDarkMode by BilindiWallRepository.instance.isDarkMode.collectAsState()
      MyApplicationTheme(darkTheme = isDarkMode) {
        BilindiWallApp()
      }
    }
  }
}


