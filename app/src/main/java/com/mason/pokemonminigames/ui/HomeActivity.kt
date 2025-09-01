package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        // Game buttons
        binding.btnGame1.setOnClickListener {
            startActivity(Intent(this, Game1Activity::class.java))
        }
        binding.btnGame2.setOnClickListener {
            startActivity(Intent(this, Game2Activity::class.java))
        }
        binding.btnGame3.setOnClickListener {
            startActivity(Intent(this, Game3Activity::class.java))
        }


        // Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
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
