package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.IkeaFoodDatabase
import com.example.data.IkeaFoodRepository
import com.example.data.IkeaFoodViewModel
import com.example.data.IkeaFoodViewModelFactory
import com.example.ui.IkeaWmsApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup database and repository
        val database = IkeaFoodDatabase.getDatabase(applicationContext)
        val repository = IkeaFoodRepository(database.dao)
        val viewModel: IkeaFoodViewModel by viewModels {
            IkeaFoodViewModelFactory(repository)
        }

        enableEdgeToEdge()
        setContent {
            // Dark mode state starting with false
            var isDarkTheme by remember { mutableStateOf(false) }
            
            MyApplicationTheme(darkTheme = isDarkTheme) {
                IkeaWmsApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}
