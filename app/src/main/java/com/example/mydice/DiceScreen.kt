//DiceScreen.kt

package com.example.mydice

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

@Composable
fun DiceScreen(navController: NavController, viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    var rotationState by remember { mutableStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = tween(durationMillis = 500),
        label = "diceRotation"
    )

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

        // Dice Area
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        rotationY = rotation
                    }
            ) {
                // 1. Base Dice Image (Stays the same)
                Image(
                    painter = painterResource(id = viewModel.getDiceImageResource(gameState.currentDieValue)),
                    contentDescription = "Dice showing ${gameState.currentDieValue}",
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Overlay Image (Logic is now much smarter)
                viewModel.getOverlayImageResource()?.let { overlayRes ->

                    // Determine the modifier based on the equipped overlay ID
                    val overlayModifier = when (gameState.equippedOverlayId) {
                        "overlay_party_hat" -> Modifier
                            .align(Alignment.TopCenter) // Position at the top
                            .fillMaxWidth(0.7f) // Make it 70% of the dice width
                            .padding(top = 8.dp) // Give it a little space from the top edge

                        "overlay_sunglasses" -> Modifier
                            .align(Alignment.Center) // Position in the center
                            .fillMaxWidth(0.9f) // Make them almost as wide as the dice

                        else -> Modifier.fillMaxSize() // A fallback, though you can customize this
                    }

                    Image(
                        painter = painterResource(id = overlayRes),
                        contentDescription = "Dice Overlay",
                        modifier = overlayModifier // Use our new dynamic modifier
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "You rolled a ${gameState.currentDieValue}!",
                style = MaterialTheme.typography.headlineMedium
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