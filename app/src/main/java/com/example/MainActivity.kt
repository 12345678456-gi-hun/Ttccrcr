package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.ui.IdeScreen
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Initialize Room Database
    val database = AppDatabase.getDatabase(this)
    val factory = MainViewModelFactory(
      database.workspaceDao(),
      database.projectFileDao(),
      database.settingDao()
    )

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
          IdeScreen(viewModel = viewModel)
        }
      }
    }
  }
}
