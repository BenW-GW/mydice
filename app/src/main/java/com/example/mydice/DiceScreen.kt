package com.example.mydice

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@DrawableRes
fun getDiceResourceForValue(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice1
        2 -> R.drawable.dice2
        3 -> R.drawable.dice3
        4 -> R.drawable.dice4
        5 -> R.drawable.dice5
        else -> R.drawable.dice6
    }
}

@Composable
fun DiceScreen(navController: NavController, viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    var rotationState by remember { mutableStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = tween(durationMillis = 500),
        label = "diceRotation"
    )

    val rollResultText = "You rolled: ${gameState.lastRollValues.joinToString(", ")}"
    val rollSum = gameState.lastRollValues.sum()
    val pointsText = if (gameState.activeMultiplier > 1) {
        "Total: $rollSum x${gameState.activeMultiplier} = ${rollSum * gameState.activeMultiplier} Points!"
    } else {
        "Total: $rollSum Points!"
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar with Score and Shop Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Text(

                    text = "Points: ${gameState.totalPoints}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Button(onClick = { navController.navigate("shop_screen") }) {
                Text("Shop")
            }
        }

        // Dice Area - Updated for multiple dice
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .graphicsLayer { rotationY = rotation },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop through the rolled dice values and display each one
                gameState.lastRollValues.forEachIndexed { index, value ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getDiceResourceForValue(value)),
                            contentDescription = "Dice showing $value",
                            modifier = Modifier.fillMaxSize()
                        )
                        // Show overlay only on the first die for simplicity
                        if (index == 0) {
                            viewModel.getOverlayImageResource()?.let { overlayRes ->
                                val overlayModifier = when (gameState.equippedOverlayId) {
                                    "overlay_party_hat" -> Modifier.align(Alignment.TopCenter).fillMaxWidth(0.7f).padding(top = 4.dp)
                                    "overlay_sunglasses" -> Modifier.align(Alignment.Center).fillMaxWidth(0.9f)
                                    else -> Modifier.fillMaxSize()
                                }
                                Image(
                                    painter = painterResource(id = overlayRes),
                                    contentDescription = "Dice Overlay",
                                    modifier = overlayModifier
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = rollResultText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = pointsText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        // Roll Button
        Button(
            onClick = {
                viewModel.rollDie()
                rotationState += 360f // Trigger rotation animation
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Roll Die", fontSize = 20.sp)
        }
    }
}