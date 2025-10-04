package com.mason.pokemonminigames.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        setupStreakReward()
        setupAchievementRewards()
        setupMysteryBox()
        setupQuizReward()
        setupEventReward()
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

    private fun setupStreakReward() {
        // streak code here
        val lastLoginDate = prefs.getString("last_login_date", "")
        val streakCount = prefs.getInt("login_streak", 0)
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        if (lastLoginDate != today) {
            val calendar = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(yesterday.time)

            val newStreak = if (lastLoginDate == yesterdayStr) streakCount + 1 else 1
            prefs.edit().putInt("login_streak", newStreak).putString("last_login_date", today).apply()

            if (newStreak % 5 == 0) {
                user.coins += 200
                saveUser()
                Toast.makeText(this, "🔥 ${newStreak}-day streak! +200 coins!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAchievementRewards() {
        // achievements code here
        val totalGamesPlayed = prefs.getInt("games_played", 0)
        val totalCaught = prefs.getInt("pokemon_caught", 0)

        if (totalGamesPlayed >= 10) rewards.add(Reward("Gamer Lv.1", "Played 10 games - +100 coins", false))
        if (totalCaught >= 5) rewards.add(Reward("Collector Lv.1", "Caught 5 Pokémon - +150 coins", false))
    }

    private fun setupMysteryBox() {
        // mystery box code here
        val lastMystery = prefs.getString("last_mystery_claim", "")
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        if (lastMystery == today) {
            binding.btnMysteryBox.isEnabled = false
            binding.btnMysteryBox.text = "Mystery Box Claimed"
        }

        binding.btnMysteryBox.setOnClickListener {
            val rewardsList = listOf(50, 100, 150, 300)
            val reward = rewardsList.random()
            user.coins += reward
            saveUser()

            prefs.edit().putString("last_mystery_claim", today).apply()
            binding.btnMysteryBox.isEnabled = false
            binding.btnMysteryBox.text = "Mystery Box Claimed"

            Toast.makeText(this, "🎁 You got $reward coins!", Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }

    private fun setupQuizReward() {
        binding.btnQuizReward.setOnClickListener {
            // Expanded and varied question pool
            val questions = listOf(
                Pair("Which Pokémon is Pikachu’s evolved form?", "Raichu"),
                Pair("What type is Charmander?", "Fire"),
                Pair("What color is Bulbasaur?", "Green"),
                Pair("Which Pokémon is known for sleeping all day?", "Snorlax"),
                Pair("Which Pokémon evolves into Gyarados?", "Magikarp"),
                Pair("What type of Pokémon is Jigglypuff?", "Fairy"),
                Pair("Which Pokémon says its own name as 'Bulba-Bulba'?", "Bulbasaur"),
                Pair("What does Eevee evolve into with a Water Stone?", "Vaporeon"),
                Pair("Who is Ash Ketchum’s first Pokémon?", "Pikachu"),
                Pair("Which Pokémon has a flame at the tip of its tail?", "Charmander"),
                Pair("What’s the evolved form of Squirtle?", "Wartortle"),
                Pair("Which Pokémon can only say 'Meowth'?", "Meowth"),
                Pair("Which Pokémon uses 'Thunderbolt' attack?", "Pikachu"),
                Pair("Which Pokémon can evolve into nine different forms?", "Eevee"),
                Pair("What color is Jigglypuff?", "Pink"),
                Pair("Which Pokémon is a ghost type?", "Gengar"),
                Pair("What item is used to catch Pokémon?", "Pokeball"),
                Pair("What’s the name of the Pokémon Professor?", "Oak"),
                Pair("Which Pokémon lives in the sea and has a shell?", "Shellder"),
                Pair("Which Pokémon is famous for saying 'Pika Pika'?", "Pikachu")
            )

            // Shuffle and pick one random question
            val randomQ = questions.shuffled().first()
            val question = randomQ.first
            val correctAnswer = randomQ.second

            // Create input field
            val input = EditText(this)
            input.hint = "Your answer"
            input.setSingleLine()

            // Build dialog
            AlertDialog.Builder(this)
                .setTitle("Pokémon Quiz Challenge 🎯")
                .setMessage(question)
                .setView(input)
                .setPositiveButton("Submit") { dialog, _ ->
                    val userAnswer = input.text.toString().trim()

                    if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                        user.coins += 100
                        saveUser()
                        updateUI()
                        Toast.makeText(this, "✅ Correct! +100 coins!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ Wrong! Correct answer: $correctAnswer", Toast.LENGTH_SHORT).show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }



    private fun setupEventReward() {
        // event reward code here
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            binding.btnEventReward.visibility = View.VISIBLE
            binding.btnEventReward.setOnClickListener {
                user.coins += 250
                saveUser()
                Toast.makeText(this, "🎉 Weekend Bonus: +250 coins!", Toast.LENGTH_SHORT).show()
                binding.btnEventReward.isEnabled = false
                updateUI()
            }
        } else {
            binding.btnEventReward.visibility = View.GONE
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
