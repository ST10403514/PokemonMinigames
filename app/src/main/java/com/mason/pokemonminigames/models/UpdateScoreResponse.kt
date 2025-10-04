package com.mason.pokemonminigames.models

data class UpdateScoreResponse(
    val ok: Boolean = true,           // matches API response
    val message: String? = null,
    val error: String? = null
)

