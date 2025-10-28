package com.example.mydice.data

import androidx.annotation.DrawableRes

enum class ItemType {
    DICE_UPGRADE,  // A permanent upgrade that adds a new die
    MULTIPLIER     // An equippable item that provides a score multiplier
}

data class ShopItem(
    val id: String,
    val name: String,
    val cost: Int,
    @DrawableRes val imageRes: Int,
    val itemType: ItemType
)