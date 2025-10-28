// GameViewModel.kt

package com.example.mydice

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydice.data.ItemType
import com.example.mydice.data.ShopItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// Represents the entire state of our game (Updated)
data class GameState(
    val totalPoints: Int = 0,
    val lastRollValues: List<Int> = listOf(6), // Holds the results of the last roll
    val numberOfDice: Int = 1,                 // How many dice to roll
    val activeMultiplier: Int = 1,             // Point multiplier
    val purchasedItemIds: Set<String> = emptySet(),
    val equippedOverlayId: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Shop items are now more descriptive of their function
    val shopItems = listOf(
        ShopItem("add_dice_red", "Add a Second Die", 100, R.drawable.red_dice6, ItemType.DICE_UPGRADE),
        ShopItem("add_dice_blue", "Add a Third Die", 500, R.drawable.blue_dice6, ItemType.DICE_UPGRADE),
        ShopItem("overlay_party_hat", "Party Hat (x2 Multiplier)", 250, R.drawable.party_hat, ItemType.MULTIPLIER),
        ShopItem("overlay_sunglasses", "Sunglasses (x3 Multiplier)", 800, R.drawable.sunglasses, ItemType.MULTIPLIER)
    )

    init {
        loadGame()
    }

    fun rollDie() {
        val rolledValues = List(_gameState.value.numberOfDice) { Random.nextInt(1, 7) }
        val sumOfRolls = rolledValues.sum()
        val pointsToAdd = sumOfRolls * _gameState.value.activeMultiplier
        val newTotalPoints = _gameState.value.totalPoints + pointsToAdd

        _gameState.update {
            it.copy(
                lastRollValues = rolledValues,
                totalPoints = newTotalPoints
            )
        }
        saveGame()
    }

    fun buyItem(item: ShopItem) {
        val currentState = _gameState.value
        if (currentState.totalPoints >= item.cost && item.id !in currentState.purchasedItemIds) {
            val newPoints = currentState.totalPoints - item.cost
            val newPurchasedItems = currentState.purchasedItemIds + item.id

            // Check if this is a dice upgrade and increment the number of dice
            var newNumberOfDice = currentState.numberOfDice
            if (item.itemType == ItemType.DICE_UPGRADE) {
                newNumberOfDice++
            }

            _gameState.update {
                it.copy(
                    totalPoints = newPoints,
                    purchasedItemIds = newPurchasedItems,
                    numberOfDice = newNumberOfDice
                )
            }
            saveGame()
        }
    }

    fun equipItem(item: ShopItem) {
        // This function now only handles MULTIPLIER items
        if (item.id in _gameState.value.purchasedItemIds && item.itemType == ItemType.MULTIPLIER) {
            val isCurrentlyEquipped = _gameState.value.equippedOverlayId == item.id
            val newOverlay = if (isCurrentlyEquipped) null else item.id

            _gameState.update {
                it.copy(
                    equippedOverlayId = newOverlay,
                    activeMultiplier = calculateMultiplier(newOverlay)
                )
            }
            saveGame()
        }
    }

    // Helper function to determine the multiplier from an equipped item ID
    private fun calculateMultiplier(equippedId: String?): Int {
        return when (equippedId) {
            "overlay_party_hat" -> 2
            "overlay_sunglasses" -> 3
            else -> 1 // Default multiplier
        }
    }

    fun getOverlayImageResource(): Int? {
        val equippedId = _gameState.value.equippedOverlayId ?: return null
        return shopItems.find { it.id == equippedId }?.imageRes
    }

    private fun saveGame() {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putInt("total_points", _gameState.value.totalPoints)
                // Convert list to a string for saving
                putString("last_roll_values", _gameState.value.lastRollValues.joinToString(","))
                putInt("number_of_dice", _gameState.value.numberOfDice)
                putInt("active_multiplier", _gameState.value.activeMultiplier)
                putStringSet("purchased_items", _gameState.value.purchasedItemIds)
                putString("equipped_overlay", _gameState.value.equippedOverlayId)
                apply()
            }
        }
    }

    private fun loadGame() {
        val totalPoints = sharedPreferences.getInt("total_points", 0)
        // Load the string and convert it back to a list of ints
        val lastRollsString = sharedPreferences.getString("last_roll_values", "6") ?: "6"
        val lastRollValues = lastRollsString.split(',').mapNotNull { it.toIntOrNull() }
        val numberOfDice = sharedPreferences.getInt("number_of_dice", 1)
        val activeMultiplier = sharedPreferences.getInt("active_multiplier", 1)
        val purchasedItems = sharedPreferences.getStringSet("purchased_items", emptySet()) ?: emptySet()
        val equippedOverlay = sharedPreferences.getString("equipped_overlay", null)

        _gameState.value = GameState(
            totalPoints = totalPoints,
            lastRollValues = if (lastRollValues.isEmpty()) listOf(6) else lastRollValues,
            numberOfDice = numberOfDice,
            activeMultiplier = activeMultiplier,
            purchasedItemIds = purchasedItems,
            equippedOverlayId = equippedOverlay
        )
    }
}