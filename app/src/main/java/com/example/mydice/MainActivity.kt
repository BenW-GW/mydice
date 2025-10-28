package com.example.mydice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mydice.ui.theme.MyDiceTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // The viewModel() automatically scopes the ViewModel to the NavHost,
    // so it's shared between the DiceScreen and ShopScreen.
    val gameViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = "dice_screen") {
        composable("dice_screen") {
            DiceScreen(navController = navController, viewModel = gameViewModel)
        }
        composable("shop_screen") {
            ShopScreen(navController = navController, viewModel = gameViewModel)
        }
    }
}
