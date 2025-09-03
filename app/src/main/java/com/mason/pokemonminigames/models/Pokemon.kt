package com.mason.pokemonminigames.models

data class Pokemon(
    val id: Int,
    val name: String,
    val imageResId: Int, // <- use this for setImageResource()
    val price: Int,
    var isPurchased: Boolean = false,
    var isSelected: Boolean = false
)
