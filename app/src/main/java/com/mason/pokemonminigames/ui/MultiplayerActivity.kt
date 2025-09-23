package com.mason.pokemonminigames.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mason.pokemonminigames.databinding.ActivityMultiplayerBinding

class MultiplayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Multiplayer"

        // TODO: Implement lobby creation/joining logic with Firebase Realtime DB or Firestore
        binding.btnCreateLobby.setOnClickListener {
            // Create lobby code
        }

        binding.btnJoinLobby.setOnClickListener {
            val code = binding.etLobbyCode.text.toString()
            // Join lobby using entered code
        }
    }
}
