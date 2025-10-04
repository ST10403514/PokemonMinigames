package com.mason.pokemonminigames.models

data class UpdateScoreRequest(
    val userId: String,
    val username: String?,
    val won: Boolean
)