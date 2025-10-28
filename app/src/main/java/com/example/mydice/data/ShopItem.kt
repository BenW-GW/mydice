package com.example.mydice.data

import androidx.annotation.DrawableRes

enum class ItemType {
    DICE_SKIN,
    OVERLAY
}

data class ShopItem(
    val id: String,
    val name: String,
    val cost: Int,
    @DrawableRes val imageRes: Int, // Image for the shop listing
    val itemType: ItemType
)