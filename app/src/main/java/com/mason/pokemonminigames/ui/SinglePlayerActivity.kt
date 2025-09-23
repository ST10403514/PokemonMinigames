package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.databinding.ActivitySinglePlayerBinding
import kotlin.random.Random

class SinglePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySinglePlayerBinding
    private val board = Array(9) { "" } // board state
    private var playerTurn = true // true = Player(X), false = Bot(O)

    // Pokémon images
    private val playerPokemon = R.drawable.ic_pokemon1
    private val botPokemon = R.drawable.ic_pokemon2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Single Player"

        setupBoard()

        // BACK BUTTON FUNCTIONALITY
        binding.btnBack.setOnClickListener {
            finish() // closes this activity and goes back
        }
    }


    private fun setupBoard() {
        for (i in 0 until binding.gridBoard.childCount) {
            val button = binding.gridBoard.getChildAt(i) as ImageButton
            button.setImageDrawable(null) // clear Pokémon
            button.setBackgroundResource(R.drawable.cell_background)
            button.setOnClickListener {
                if (board[i].isEmpty() && playerTurn) {
                    makeMove(button, "X", i, playerPokemon)
                    if (!checkGameOver("X")) {
                        botMove()
                    }
                }
            }
        }
    }

    private fun makeMove(button: ImageButton, symbol: String, index: Int, imageRes: Int) {
        button.setImageResource(imageRes) // Pokémon in center
        board[index] = symbol
        playerTurn = !playerTurn
    }

    private fun botMove() {
        val emptyCells = board.mapIndexed { i, cell -> if (cell.isEmpty()) i else null }
            .filterNotNull()

        if (emptyCells.isNotEmpty()) {
            val randomIndex = emptyCells[Random.nextInt(emptyCells.size)]
            val button = binding.gridBoard.getChildAt(randomIndex) as ImageButton
            makeMove(button, "O", randomIndex, botPokemon)
            checkGameOver("O")
        }
    }

    private fun checkGameOver(symbol: String): Boolean {
        val winPatterns = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (pattern in winPatterns) {
            if (board[pattern[0]] == symbol &&
                board[pattern[1]] == symbol &&
                board[pattern[2]] == symbol
            ) {
                Toast.makeText(this, "$symbol wins!", Toast.LENGTH_LONG).show()
                resetBoard()
                return true
            }
        }

        if (board.all { it.isNotEmpty() }) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show()
            resetBoard()
            return true
        }

        return false
    }

    private fun resetBoard() {
        for (i in board.indices) {
            board[i] = ""
            val button = binding.gridBoard.getChildAt(i) as ImageButton
            button.setImageDrawable(null) // clear Pokémon
            button.setBackgroundResource(R.drawable.cell_background)
        }
        playerTurn = true
    }
}
