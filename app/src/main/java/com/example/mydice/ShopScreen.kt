// REPLACE YOUR ENTIRE ShopScreen.kt FILE WITH THIS

package com.example.mydice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydice.data.ItemType
import com.example.mydice.data.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(navController: NavController, viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        ResetGameConfirmationDialog(
            onConfirm = {
                viewModel.resetGame()
                showResetDialog = false
            },
            onDismiss = {
                showResetDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = "Points: ${gameState.totalPoints}",
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Game")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.shopItems) { item ->
                ShopItemCard(item = item, gameState = gameState, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ResetGameConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Game") },
        text = { Text("Are you sure you want to reset all your progress? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reset")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ShopItemCard(item: ShopItem, gameState: GameState, viewModel: GameViewModel) {
    val isPurchased = item.id in gameState.purchasedItemIds
    val canAfford = gameState.totalPoints >= item.cost
    val isEquipped = item.itemType == ItemType.MULTIPLIER && gameState.equippedOverlayId == item.id

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            // NOTE: horizontalArrangement is now removed from here
        ) {
            // This Row now contains the weight modifier to be flexible
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // This tells the text to take up all available space
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Cost: ${item.cost} points", fontSize = 14.sp)
                }
            }

            // Add a small spacer for guaranteed padding
            Spacer(Modifier.width(8.dp))

            ShopItemButton(
                itemType = item.itemType,
                isPurchased = isPurchased,
                isEquipped = isEquipped,
                canAfford = canAfford,
                onBuyClick = { viewModel.buyItem(item) },
                onEquipClick = { viewModel.equipItem(item) }
            )
        }
    }
}

@Composable
fun ShopItemButton(
    itemType: ItemType, isPurchased: Boolean, isEquipped: Boolean, canAfford: Boolean,
    onBuyClick: () -> Unit, onEquipClick: () -> Unit
) {
    when {
        isPurchased && itemType == ItemType.DICE_UPGRADE -> {
            Button(onClick = {}, enabled = false) { Text("Purchased") }
        }
        isEquipped -> {
            Button(onClick = onEquipClick) { Text("Equipped") }
        }
        isPurchased -> {
            Button(onClick = onEquipClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Equip")
            }
        }
        else -> {
            Button(onClick = onBuyClick, enabled = canAfford) { Text("Buy") }
        }
    }
}