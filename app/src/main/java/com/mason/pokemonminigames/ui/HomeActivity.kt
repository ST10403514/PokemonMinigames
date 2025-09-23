package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mason.pokemonminigames.databinding.ActivityHomeBinding
import com.mason.pokemonminigames.R

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        binding.tvWelcome.text = if (user != null) {
            "Welcome, ${user.email ?: "Player"}"
        } else {
            "Welcome, Guest"
        }

        // ✅ Tic Tac Toe Buttons
        binding.cardSinglePlayer.setOnClickListener {
            startActivity(Intent(this, SinglePlayerActivity::class.java))
        }

        binding.cardMultiplayer.setOnClickListener {
            startActivity(Intent(this, MultiplayerActivity::class.java))
        }

        // ✅ Keep bottom nav if you still want it
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    true
                }
                R.id.nav_store -> {
                    startActivity(Intent(this, StoreActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
