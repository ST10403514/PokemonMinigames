package com.mason.pokemonminigames.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.adapters.CollectionAdapter
import com.mason.pokemonminigames.adapters.PokemonAdapter
import com.mason.pokemonminigames.databinding.ActivityStoreBinding
import com.mason.pokemonminigames.models.Pokemon
import com.mason.pokemonminigames.models.User
import org.json.JSONArray

class StoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreBinding
    private lateinit var pokemonAdapter: PokemonAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var prefs: SharedPreferences
    private val user = User()
    private val pokemons = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("pokemon_store", MODE_PRIVATE)

        setupPokemons()
        loadData() // ✅ Load saved coins + collection before adapters
        setupAdapters()
        setupBottomNav()
        updateCoinsUI()
    }

    private fun setupPokemons() {
        pokemons.addAll(
            listOf(
                Pokemon(1, "Pokemon 1", R.drawable.ic_pokemon1, 100),
                Pokemon(2, "Pokemon 2", R.drawable.ic_pokemon2, 200),
                Pokemon(3, "Pokemon 3", R.drawable.ic_pokemon3, 300),
                Pokemon(4, "Pokemon 4", R.drawable.ic_pokemon4, 400),
                Pokemon(5, "Pokemon 5", R.drawable.ic_pokemon5, 350),
                Pokemon(6, "Pokemon 6", R.drawable.ic_pokemon6, 550)
            )
        )
    }

    private fun setupAdapters() {
        pokemonAdapter = PokemonAdapter(pokemons, user) {
            updateCoinsUI()
            collectionAdapter.notifyDataSetChanged()
            saveData() // ✅ Save whenever something changes
        }
        binding.recyclerPokemons.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerPokemons.adapter = pokemonAdapter

        collectionAdapter = CollectionAdapter(user.collection)
        binding.recyclerCollection.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCollection.adapter = collectionAdapter
    }

    private fun updateCoinsUI() {
        binding.tvCoins.text = "Coins: ${user.coins}"
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_store
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
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_store -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // ✅ Save user coins + purchased Pokemon IDs
    private fun saveData() {
        val editor = prefs.edit()
        editor.putInt("coins", user.coins)

        val purchasedIds = JSONArray()
        pokemons.filter { it.isPurchased }.forEach { purchasedIds.put(it.id) }
        editor.putString("purchased_pokemon", purchasedIds.toString())

        editor.apply()
    }

    // ✅ Load user coins + purchased Pokemon IDs
    private fun loadData() {
        user.coins = prefs.getInt("coins", 500) // default starting coins
        val purchasedJson = prefs.getString("purchased_pokemon", "[]")
        val purchasedIds = JSONArray(purchasedJson)

        for (i in 0 until purchasedIds.length()) {
            val id = purchasedIds.getInt(i)
            val pokemon = pokemons.find { it.id == id }
            pokemon?.isPurchased = true
            if (pokemon != null && !user.collection.contains(pokemon)) {
                user.collection.add(pokemon)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }
}
