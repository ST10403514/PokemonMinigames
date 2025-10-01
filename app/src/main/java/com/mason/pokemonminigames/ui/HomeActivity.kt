package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mason.pokemonminigames.databinding.ActivityHomeBinding
import com.mason.pokemonminigames.R

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        binding.tvWelcome.text = if (user != null) {
            "Welcome, ${user.email ?: "Player"}"
        } else {
            "Welcome, Guest"
        }

        // Load the current user's highscore from Firestore
        loadUserHighscore()

        // Click handlers
        binding.cardSinglePlayer.setOnClickListener {
            startActivity(Intent(this, SinglePlayerActivity::class.java))
        }
        binding.cardMultiplayer.setOnClickListener {
            startActivity(Intent(this, LobbyActivity::class.java))
        }

        // Bottom nav
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

    private fun loadUserHighscore() {
        val user = auth.currentUser
        if (user == null) {
            binding.tvSingleHighscore.text = "Highscore: 0"
            return
        }

        db.collection("singleplayer_leaderboard").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val highscore = doc.getLong("highScore")?.toInt() ?: 0
                binding.tvSingleHighscore.text = "Highscore: $highscore"
            }
            .addOnFailureListener {
                binding.tvSingleHighscore.text = "Highscore: 0"
            }
    }
}
