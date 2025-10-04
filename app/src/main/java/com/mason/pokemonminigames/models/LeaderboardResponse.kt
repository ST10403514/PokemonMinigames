package com.mason.pokemonminigames.models

data class LeaderboardEntry(
    val userId: String = "",
    val username: String = "Player",
    val highScore: Int = 0,
    val updatedAt: String? = null
)

data class LeaderboardResponse(
    val ok: Boolean = true,
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val error: String? = null
)
