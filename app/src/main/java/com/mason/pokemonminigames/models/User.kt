package com.mason.pokemonminigames.models

import kotlin.random.Random

data class User(
    val username: String = "Player",
    val highScore: Int = 0,
    var coins: Int = 500,
    var points: Int = 0,
    val uid:String ="",
    var collection: MutableList<Pokemon> = mutableListOf()
) {
    companion object {
        // Create a User with an auto-generated username from email prefix + 3 digits
        fun fromEmail(email: String?): User {
            val base = email?.substringBefore("@") ?: "Player"
            val randomDigits = Random.nextInt(100, 999)
            return User(username = "${base}${randomDigits}", highScore = 0)
        }
    }
}
