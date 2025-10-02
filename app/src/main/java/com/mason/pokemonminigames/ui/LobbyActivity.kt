package com.mason.pokemonminigames.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mason.pokemonminigames.databinding.ActivityLobbyBinding
import java.util.*

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Multiplayer"

        enableLobbyButtons(false)
        initAuth()

        binding.btnCreateLobby.setOnClickListener { createLobby() }
        binding.btnJoinLobby.setOnClickListener { joinLobby() }
    }

    private fun initAuth() {
        val current = auth.currentUser
        if (current != null) {
            userId = current.uid
            enableLobbyButtons(true)
        } else {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userId = auth.currentUser?.uid ?: ""
                    if (userId.isNotEmpty()) enableLobbyButtons(true)
                    else {
                        Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_LONG).show()
                        Log.e("LOBBY", "Anonymous sign-in succeeded but UID empty")
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Firebase Auth failed: ${task.exception?.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("LOBBY", "Firebase Auth failed", task.exception)
                }
            }
        }
    }

    private fun enableLobbyButtons(enabled: Boolean) {
        binding.btnCreateLobby.isEnabled = enabled
        binding.btnJoinLobby.isEnabled = enabled
    }

    private fun createLobby() {
        if (userId.isEmpty()) return

        val progress = ProgressDialog(this)
        progress.setMessage("Creating lobby...")
        progress.setCancelable(false)
        progress.show()

        val lobbyCode = generateLobbyCode()
        val lobbyRef = database.collection("lobbies").document(lobbyCode)

        val lobbyData = hashMapOf(
            "hostId" to userId,
            "guestId" to "",
            "status" to "waiting",
            "board" to hashMapOf(
                "0" to hashMapOf("0" to "", "1" to "", "2" to ""),
                "1" to hashMapOf("0" to "", "1" to "", "2" to ""),
                "2" to hashMapOf("0" to "", "1" to "", "2" to "")
            ),
            "currentPlayer" to "X",
            "winner" to null
        )

        lobbyRef.set(lobbyData)
            .addOnSuccessListener {
                progress.dismiss()
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Lobby Created")
                    .setMessage("Share this code with your friend:\n\n$lobbyCode")
                    .setPositiveButton("OK") { _, _ -> navigateToGame(lobbyCode) }
                    .setCancelable(false)
                    .show()
            }
            .addOnFailureListener {
                progress.dismiss()
                Toast.makeText(this, "Failed to create lobby: ${it.message}", Toast.LENGTH_LONG).show()
                Log.e("LOBBY", "Create lobby failed", it)
            }
    }


    private fun joinLobby() {
        if (userId.isEmpty()) return

        val code = binding.etLobbyCode.text.toString().trim().uppercase()
        if (code.isEmpty()) {
            Toast.makeText(this, "Enter a lobby code", Toast.LENGTH_SHORT).show()
            return
        }

        val progress = ProgressDialog(this)
        progress.setMessage("Joining lobby...")
        progress.setCancelable(false)
        progress.show()

        val lobbyRef = database.collection("lobbies").document(code)
        lobbyRef.get()
            .addOnSuccessListener { snapshot ->
                progress.dismiss()
                if (!snapshot.exists()) {
                    Toast.makeText(this, "Lobby not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val status = snapshot.getString("status") ?: ""
                val guestId = snapshot.getString("guestId") ?: ""

                if (status == "waiting" && guestId.isEmpty()) {
                    lobbyRef.update(
                        mapOf(
                            "guestId" to userId,
                            "status" to "started"
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Joined lobby: $code", Toast.LENGTH_SHORT).show()
                        navigateToGame(code)
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to join lobby: ${it.message ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("LOBBY", "Join lobby update failed", it)
                    }
                } else {
                    Toast.makeText(this, "Lobby is full or already started", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                progress.dismiss()
                Toast.makeText(this, "Error accessing lobby: ${it.message ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                Log.e("LOBBY", "Error fetching lobby", it)
            }
    }

    private fun navigateToGame(lobbyCode: String) {
        val intent = Intent(this, MultiplayerGameActivity::class.java)
        intent.putExtra("LOBBY_CODE", lobbyCode)
        startActivity(intent)
    }

    private fun generateLobbyCode(): String {
        return UUID.randomUUID().toString().substring(0, 6).uppercase()
    }
}
