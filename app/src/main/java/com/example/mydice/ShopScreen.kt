package com.example.mydice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
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
fun ShopItemCard(item: ShopItem, gameState: GameState, viewModel: GameViewModel) {
    val isPurchased = item.id in gameState.purchasedItemIds
    val canAfford = gameState.totalPoints >= item.cost
    val isEquipped = when(item.itemType) {
        ItemType.DICE_SKIN -> gameState.equippedDiceSkinId == item.id
        ItemType.OVERLAY -> gameState.equippedOverlayId == item.id
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

            ShopItemButton(
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
    isPurchased: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onBuyClick: () -> Unit,
    onEquipClick: () -> Unit
) {
    when {
        isEquipped -> {
            Button(onClick = onEquipClick,
                // If it's an overlay, clicking again unequips it. Dice skins can't be unequipped, only replaced.
                // For simplicity, we just let both be "clicked" again.
            ) {
                Text("Equipped")
            }
        }
        isPurchased -> {
            Button(onClick = onEquipClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Equip")
            }
        }
        else -> { // Not purchased
            Button(onClick = onBuyClick, enabled = canAfford) {
                Text("Buy")
            }
        }
    }
}