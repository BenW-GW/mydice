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

// Represents the entire state of our game
data class GameState(
    val totalPoints: Int = 0,
    val currentDieValue: Int = 6,
    val purchasedItemIds: Set<String> = emptySet(),
    val equippedDiceSkinId: String = "dice_white",
    val equippedOverlayId: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val shopItems = listOf(
        ShopItem("dice_red", "Red Dice", 100, R.drawable.red_dice6, ItemType.DICE_SKIN),
        ShopItem("dice_blue", "Blue Dice", 100, R.drawable.blue_dice6, ItemType.DICE_SKIN),
        ShopItem("overlay_party_hat", "Party Hat", 250, R.drawable.party_hat, ItemType.OVERLAY),
        ShopItem("overlay_sunglasses", "Sunglasses", 300, R.drawable.sunglasses, ItemType.OVERLAY)
        // Add more items here
    )

    init {
        loadGame()
    }

    fun rollDie() {
        val rolledValue = Random.nextInt(1, 7)
        val newPoints = _gameState.value.totalPoints + rolledValue
        _gameState.update { it.copy(currentDieValue = rolledValue, totalPoints = newPoints) }
        saveGame()
    }

    fun buyItem(item: ShopItem) {
        if (_gameState.value.totalPoints >= item.cost && item.id !in _gameState.value.purchasedItemIds) {
            val newPoints = _gameState.value.totalPoints - item.cost
            val newPurchasedItems = _gameState.value.purchasedItemIds + item.id
            _gameState.update { it.copy(totalPoints = newPoints, purchasedItemIds = newPurchasedItems) }
            saveGame()
        }
    }

    fun equipItem(item: ShopItem) {
        if (item.id in _gameState.value.purchasedItemIds) {
            when (item.itemType) {
                ItemType.DICE_SKIN -> {
                    _gameState.update { it.copy(equippedDiceSkinId = item.id) }
                }
                ItemType.OVERLAY -> {
                    // Unequip if it's already equipped, otherwise equip it
                    val newOverlay = if (_gameState.value.equippedOverlayId == item.id) null else item.id
                    _gameState.update { it.copy(equippedOverlayId = newOverlay) }
                }
            }
            saveGame()
        }
    }

    fun getDiceImageResource(dieValue: Int): Int {
        val prefix = _gameState.value.equippedDiceSkinId.replace("dice_", "")
        val resourceName = if (prefix == "white") "dice$dieValue" else "${prefix}_dice$dieValue"
        return getApplication<Application>().resources.getIdentifier(
            resourceName, "drawable", getApplication<Application>().packageName
        )
    }

    fun getOverlayImageResource(): Int? {
        return when (_gameState.value.equippedOverlayId) {
            "overlay_party_hat" -> R.drawable.party_hat
            "overlay_sunglasses" -> R.drawable.sunglasses
            else -> null
        }
    }

    private fun saveGame() {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putInt("total_points", _gameState.value.totalPoints)
                putStringSet("purchased_items", _gameState.value.purchasedItemIds)
                putString("equipped_skin", _gameState.value.equippedDiceSkinId)
                putString("equipped_overlay", _gameState.value.equippedOverlayId)
                apply()
            }
        }
    }

    private fun loadGame() {
        val totalPoints = sharedPreferences.getInt("total_points", 0)
        val purchasedItems = sharedPreferences.getStringSet("purchased_items", emptySet()) ?: emptySet()
        val equippedSkin = sharedPreferences.getString("equipped_skin", "dice_white") ?: "dice_white"
        val equippedOverlay = sharedPreferences.getString("equipped_overlay", null)

        _gameState.value = GameState(
            totalPoints = totalPoints,
            purchasedItemIds = purchasedItems,
            equippedDiceSkinId = equippedSkin,
            equippedOverlayId = equippedOverlay
        )
    }
}