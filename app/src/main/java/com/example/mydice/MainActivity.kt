package com.example.mydice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    // The viewModel() delegate automatically scopes the ViewModel to the NavHost,
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

@Composable
fun Dice(){
    var dieIndex by remember { mutableIntStateOf(5) }
    var dieValue by remember {mutableIntStateOf(6)}
    val dieArray=arrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )
    // Rotation animation



    Column(
        modifier = Modifier.fillMaxSize(),
        // verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter= painterResource(id=dieArray[dieIndex]),
            contentDescription = "Dice image",
            modifier = Modifier
                .size(150.dp)


        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick={
            dieIndex=rollDie()
            dieValue=dieIndex+1

        }

        )
        { Text("Roll Die")}
        Spacer(modifier = Modifier.height(24.dp))

        Text("$dieValue")
    }
}


fun rollDie():Int{
    val roll=(0..5).random()
    return roll
}



@Preview(showBackground = true)
@Composable
fun DicePreview() {
    MyDiceTheme {
        Dice()
    }
}