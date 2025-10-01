package com.mason.pokemonminigames.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mason.pokemonminigames.databinding.ActivityLobbyBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

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

        if (auth.currentUser == null){
            auth.signInAnonymously().addOnCompleteListener {
                 if (it.isSuccessful){
                     userId = auth.currentUser!!.uid
                 }
            }
        } else {
            userId = auth.currentUser!!.uid
        }

        // TODO: Implement lobby creation/joining logic with Firebase Realtime DB or Firestore
        binding.btnCreateLobby.setOnClickListener {
            // Create lobby code
            val lobbyCode = generateLobbyCode()
            val lobbyRef = database.collection("lobbies").document(lobbyCode)

            val lobbyData = mapOf(
                "hostId" to userId,
                "guestId" to "",
                "status" to "waiting",
                "board" to listOf(
                    listOf("", "", ""),
                    listOf("", "", ""),
                    listOf("", "", "")
                ),
                "currentPlayer" to "X"
            )

            lobbyRef.set(lobbyData).addOnSuccessListener {
                Toast.makeText(this, "Lobby created. Code: $lobbyCode", Toast.LENGTH_LONG).show()
                // TODO: Navigate to game screen, pass lobbyCode
            }
        }

        binding.btnJoinLobby.setOnClickListener {
            val code = binding.etLobbyCode.text.toString()
            // Join lobby using entered code
            if (code.isEmpty()){
                Toast.makeText(this, "Enter a lobby code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lobbyRef = database.collection("lobbies").document(code)

            lobbyRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val status = snapshot.getString("status") ?: ""
                    if (status == "waiting"){
                        // Update fields
                        lobbyRef.update(
                            mapOf(
                                "guestId" to userId,
                                "status" to "full"
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Joined lobby: $code", Toast.LENGTH_SHORT).show()
                            // TODO: Navigate to game screen, pass lobbyCode
                        }
                    } else {
                        Toast.makeText(this, "Lobby is full or in-game", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Lobby not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateLobbyCode(): String{
        // Short unique code (like 6 chars)
        return UUID.randomUUID().toString().substring(0, 6).uppercase()
    }
}
