package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.databinding.ActivitySinglePlayerBinding
import kotlin.random.Random

class SinglePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySinglePlayerBinding
    private val board = Array(9) { "" }
    private var playerTurn = true
    private var currentStreak = 0
    private var bestStreak = 0

    private val playerPokemon = R.drawable.ic_pokemon1
    private val botPokemon = R.drawable.ic_pokemon2

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Single Player"

        // Load current user's highscore from Firestore
        loadUserHighscore()

        setupBoard()
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadUserHighscore() {
        val user = auth.currentUser ?: return
        db.collection("singleplayer_leaderboard").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                bestStreak = doc.getLong("highScore")?.toInt() ?: 0
                updateScoreUI()
            }
            .addOnFailureListener {
                bestStreak = 0
                updateScoreUI()
            }
    }

    private fun setupBoard() {
        for (i in 0 until binding.gridBoard.childCount) {
            val button = binding.gridBoard.getChildAt(i) as ImageButton
            button.setImageDrawable(null)
            button.setBackgroundResource(R.drawable.cell_background)
            button.setOnClickListener {
                if (board[i].isEmpty() && playerTurn) {
                    makeMove(button, "X", i, playerPokemon)
                    if (!checkGameOver("X")) botMove()
                }
            }
        }
    }

    private fun makeMove(button: ImageButton, symbol: String, index: Int, imageRes: Int) {
        button.setImageResource(imageRes)
        board[index] = symbol
        playerTurn = !playerTurn
    }

    private fun botMove() {
        val emptyCells = board.mapIndexed { i, cell -> if (cell.isEmpty()) i else null }.filterNotNull()
        if (emptyCells.isNotEmpty()) {
            val randomIndex = emptyCells.random()
            val button = binding.gridBoard.getChildAt(randomIndex) as ImageButton
            makeMove(button, "O", randomIndex, botPokemon)
            checkGameOver("O")
        }
    }

    private fun checkGameOver(symbol: String): Boolean {
        val winPatterns = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8),
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8),
            intArrayOf(0,4,8), intArrayOf(2,4,6)
        )

        var gameOver = false
        for (p in winPatterns) {
            if (board[p[0]] == symbol && board[p[1]] == symbol && board[p[2]] == symbol) {
                if (symbol == "X") {
                    currentStreak++
                    if (currentStreak > bestStreak) bestStreak = currentStreak
                    Toast.makeText(this, "You win! Streak: $currentStreak", Toast.LENGTH_SHORT).show()
                } else {
                    currentStreak = 0
                    Toast.makeText(this, "Bot wins! Streak reset", Toast.LENGTH_SHORT).show()
                }
                gameOver = true
                break
            }
        }

        if (!gameOver && board.all { it.isNotEmpty() }) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_SHORT).show()
            gameOver = true
        }

        if (gameOver) {
            updateScoreUI()
            saveHighScore(bestStreak)
            resetBoard()
        }

        return gameOver
    }

    private fun resetBoard() {
        for (i in board.indices) {
            board[i] = ""
            val button = binding.gridBoard.getChildAt(i) as ImageButton
            button.setImageDrawable(null)
            button.setBackgroundResource(R.drawable.cell_background)
        }
        playerTurn = true
    }

    private fun updateScoreUI() {
        binding.tvHighscore.text = "Highscore: $bestStreak"
        binding.tvCurrentStreak.text = "Current Streak: $currentStreak"
    }

    private fun saveHighScore(highScore: Int) {
        val user = auth.currentUser ?: return
        val username = user.email?.substringBefore("@") ?: "Player"

        db.collection("singleplayer_leaderboard").document(user.uid)
            .set(mapOf("username" to username, "highScore" to highScore))
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save highscore", Toast.LENGTH_SHORT).show()
            }
    }
}
