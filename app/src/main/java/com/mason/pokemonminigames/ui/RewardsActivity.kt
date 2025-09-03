package com.mason.pokemonminigames.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.adapters.RewardAdapter
import com.mason.pokemonminigames.databinding.ActivityRewardsBinding
import com.mason.pokemonminigames.models.Reward
import com.mason.pokemonminigames.models.User
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var rewardAdapter: RewardAdapter
    private val user = User()
    private val rewards = mutableListOf<Reward>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("pokemon_store", MODE_PRIVATE)

        loadUser()
        setupRewards()
        setupRecycler()
        setupDailyLogin()
        setupBottomNav()
        updateUI()
    }

    private fun loadUser() {
        user.coins = prefs.getInt("coins", 500)
        // you can also load points later when you add gameplay scoring
    }

    private fun saveUser() {
        prefs.edit().putInt("coins", user.coins).apply()
    }

    private fun setupRewards() {
        // Example rewards
        rewards.addAll(
            listOf(
                Reward("Win 3 Games", "Earned 50 coins", false),
                Reward("Collect 2 Pokémon", "Bonus 100 coins", false),
                Reward("First Login", "Starter reward", true) // claimed already
            )
        )
    }

    private fun setupRecycler() {
        rewardAdapter = RewardAdapter(rewards) { reward ->
            if (!reward.isClaimed) {
                reward.isClaimed = true
                user.coins += 50 // you can vary this
                saveUser()
                updateUI()
                Toast.makeText(this, "Reward claimed!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerRewards.layoutManager = LinearLayoutManager(this)
        binding.recyclerRewards.adapter = rewardAdapter

        binding.btnLoadMore.setOnClickListener {
            val newRewards = listOf(
                Reward("Play 5 Games", "Earn 200 coins", false),
                Reward("Catch Rare Pokémon", "Mystery bonus!", false)
            )
            rewards.addAll(newRewards)
            rewardAdapter.notifyDataSetChanged()
        }
    }

    private fun setupDailyLogin() {
        val lastClaim = prefs.getString("last_daily_claim", "")
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        if (lastClaim == today) {
            binding.btnDailyClaim.isEnabled = false
            binding.btnDailyClaim.text = "Already Claimed"
        }

        binding.btnDailyClaim.setOnClickListener {
            val now = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            prefs.edit().putString("last_daily_claim", now).apply()

            user.coins += 150
            saveUser()
            updateUI()

            binding.btnDailyClaim.isEnabled = false
            binding.btnDailyClaim.text = "Already Claimed"

            Toast.makeText(this, "Daily 150 coins added!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        binding.tvCoins.text = "Coins: ${user.coins}"
        binding.tvPoints.text = "Points: ${user.points}" // extend User model with `points`
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_rewards
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_rewards -> true
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
