package com.mason.pokemonminigames.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
        setupLanguageSpinner()
        loadSettings()

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_settings

        binding.bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
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
                R.id.nav_store -> {
                    startActivity(Intent(this, StoreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("English", "Afrikaans", "Zulu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        binding.spinnerLanguage.adapter = adapter
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("username", binding.etUsername.text.toString())
            putString("password", binding.etPassword.text.toString())
            putString("language", binding.spinnerLanguage.selectedItem.toString())
            putBoolean("notifications", binding.cbNotifications.isChecked)
            putBoolean("biometrics", binding.cbBiometrics.isChecked)
            apply()
        }
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        binding.etUsername.setText(prefs.getString("username", ""))
        binding.etPassword.setText(prefs.getString("password", ""))
        binding.cbNotifications.isChecked = prefs.getBoolean("notifications", false)
        binding.cbBiometrics.isChecked = prefs.getBoolean("biometrics", false)

        val language = prefs.getString("language", "English")
        val position = (binding.spinnerLanguage.adapter as ArrayAdapter<String>).getPosition(language)
        binding.spinnerLanguage.setSelection(position)
    }
}
