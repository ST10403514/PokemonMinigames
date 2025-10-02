package com.mason.pokemonminigames.models

data class MultiplayerLobby(
    var hostId: String = "",
    var guestId: String = "",
    var status: String = "waiting",
    var board: Map<String, Map<String, String>> = hashMapOf(),
    var currentPlayer: String = "X",
    var winner: String? = null
)


