package com.mason.pokemonminigames.models

data class Avatar(
    val id: Int,
    val name: String,
    val price: Int,
    val imageRes: Int,
    var isOwned: Boolean = false,
    var isSelected: Boolean = false
)
