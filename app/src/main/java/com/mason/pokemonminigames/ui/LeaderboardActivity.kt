package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mason.pokemonminigames.databinding.ActivityLeaderboardBinding
import com.mason.pokemonminigames.adapters.LeaderboardPagerAdapter
import com.mason.pokemonminigames.R
class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val tabTitles = arrayOf("Singleplayer", "Multiplayer")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager with tabs
        binding.viewPager.adapter = LeaderboardPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabTitles[pos]
        }.attach()

        // Setup bottom navigation
        binding.bottomNav.selectedItemId = R.id.nav_leaderboard
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_leaderboard -> true
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_store -> {
                    startActivity(Intent(this, StoreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
