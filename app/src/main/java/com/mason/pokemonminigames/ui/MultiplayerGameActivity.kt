package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.models.MultiplayerLobby

class MultiplayerGameActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvStatus: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var cells: Array<ImageButton>

    private lateinit var lobbyCode: String
    private var currentPlayerId = ""
    private var mySymbol = ""
    private var opponentSymbol = ""
    private var isMyTurn = false
    private var lobbyListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_game)

        tvStatus = findViewById(R.id.tvStatus)
        btnBack = findViewById(R.id.btnBack)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to play!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentPlayerId = currentUser.uid

        // Initialize 3x3 board buttons safely
        cells = Array(9) { i ->
            findViewById(resources.getIdentifier("cell$i", "id", packageName))
        }
        cells.forEachIndexed { i, button ->
            button.setOnClickListener { onCellClicked(i) }
        }

        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Leave Game?")
                .setMessage("Are you sure you want to leave this game?")
                .setPositiveButton("Yes") { _, _ -> finish() }
                .setNegativeButton("No", null)
                .show()
        }

        lobbyCode = intent.getStringExtra("LOBBY_CODE") ?: run {
            Toast.makeText(this, "Lobby code missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        listenForLobbyUpdates()
    }

    private fun listenForLobbyUpdates() {
        val lobbyRef = db.collection("lobbies").document(lobbyCode)
        lobbyListener = lobbyRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, "Error fetching lobby: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("MULTIPLAYER", "Firestore error", error)
                return@addSnapshotListener
            }
            if (snapshot == null || !snapshot.exists()) return@addSnapshotListener

            val lobby = snapshot.toObject(MultiplayerLobby::class.java) ?: return@addSnapshotListener

            mySymbol = if (currentPlayerId == lobby.hostId) "X" else "O"
            opponentSymbol = if (mySymbol == "X") "O" else "X"
            isMyTurn = lobby.currentPlayer == mySymbol

            // Convert Firestore board map to 2D List for UI
            val board: List<List<String>> = (0..2).map { row ->
                (0..2).map { col ->
                    lobby.board[row.toString()]?.get(col.toString()) ?: ""
                }
            }

            updateBoardUI(board)

            when (lobby.status) {
                "waiting" -> {
                    tvStatus.text = "Waiting for player 2..."
                    enableBoard(false, board)
                }
                "started" -> {
                    tvStatus.text = if (isMyTurn) "Your Turn ($mySymbol)!" else "Opponent's Turn..."
                    enableBoard(isMyTurn, board)
                }
                "finished" -> {
                    enableBoard(false, board)
                    val message = when (lobby.winner) {
                        currentPlayerId -> "You Win! 🎉"
                        null -> "It's a Draw!"
                        else -> "You Lose!"
                    }
                    tvStatus.text = "Game Over"
                    showGameOver(message)
                }
            }
        }
    }

    private fun onCellClicked(index: Int) {
        if (!isMyTurn) return

        val row = index / 3
        val col = index % 3
        val lobbyRef = db.collection("lobbies").document(lobbyCode)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(lobbyRef)
            val lobby = snapshot.toObject(MultiplayerLobby::class.java) ?: return@runTransaction

            // Convert board map to mutable 2D list
            val currentBoard = (0..2).map { r ->
                (0..2).map { c ->
                    lobby.board[r.toString()]?.get(c.toString()) ?: ""
                }.toMutableList()
            }.toMutableList()

            if (currentBoard[row][col].isNotEmpty()) return@runTransaction

            currentBoard[row][col] = mySymbol

            // Convert 2D list back to Map<String, Map<String, String>>
            val newBoardMap = (0..2).associate { r ->
                r.toString() to (0..2).associate { c ->
                    c.toString() to currentBoard[r][c]
                }
            }

            lobby.board = newBoardMap

            // Check win/draw
            if (checkWin(currentBoard, mySymbol)) {
                lobby.status = "finished"
                lobby.winner = currentPlayerId
            } else if (currentBoard.all { it.all { cell -> cell.isNotEmpty() } }) {
                lobby.status = "finished"
                lobby.winner = null
            } else {
                lobby.currentPlayer = if (lobby.currentPlayer == "X") "O" else "X"
                lobby.status = "started"
            }

            transaction.set(lobbyRef, lobby)
        }.addOnFailureListener {
            Toast.makeText(this, "Move failed: ${it.message}", Toast.LENGTH_SHORT).show()
            Log.e("MULTIPLAYER", "Transaction failed", it)
        }
    }

    private fun updateBoardUI(board: List<List<String>>) {
        for (i in 0 until 9) {
            val row = i / 3
            val col = i % 3
            val button = cells[i]
            when (board[row][col]) {
                "X" -> button.setImageResource(R.drawable.ic_pokemon3)
                "O" -> button.setImageResource(R.drawable.ic_pokemon1)
                else -> button.setImageDrawable(null)
            }
        }
    }

    private fun enableBoard(enable: Boolean, board: List<List<String>>) {
        for (i in cells.indices) {
            val row = i / 3
            val col = i % 3
            cells[i].isEnabled = enable && board[row][col].isEmpty()
        }
    }

    private fun checkWin(board: List<List<String>>, symbol: String): Boolean {
        val winPatterns = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8),
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8),
            intArrayOf(0,4,8), intArrayOf(2,4,6)
        )
        return winPatterns.any { pattern ->
            pattern.all { i ->
                val row = i / 3
                val col = i % 3
                board[row][col] == symbol
            }
        }
    }

    private fun showGameOver(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        lobbyListener?.remove()
    }
}
