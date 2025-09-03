package com.mason.pokemonminigames.models

data class User(
    var coins: Int = 500,
    var points: Int = 0,
    var collection: MutableList<Pokemon> = mutableListOf()
)
