package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mason.pokemonminigames.R
import java.util.Locale
import java.util.UUID

class MultiPlayerActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var lobbyCode: String
    private lateinit var playerSymbol: String
    private lateinit var opponentSymbol: String
    private lateinit var cells: Array<ImageButton>
    private lateinit var tvHighscore: TextView
    private lateinit var tvCurrentStreak: TextView

    private var myTurn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_player)

        database = FirebaseDatabase.getInstance().reference

        tvHighscore = findViewById(R.id.tvHighscore)
        tvCurrentStreak = findViewById(R.id.tvCurrentStreak)

        cells = Array(9) { i ->
            findViewById(
                resources.getIdentifier("cell$i", "id", packageName)
            )
        }

        lobbyCode = intent.getStringExtra("LOBBY_CODE") ?: generateLobbyCode()

        joinOrCreateLobby()

        for (i in cells.indices){
            cells[i].setOnClickListener{
                if(myTurn){
                    makeMove(i)
                } else {
                    Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun joinOrCreateLobby(){
        val lobbyRef = database.child("lobbies").child(lobbyCode)

        lobbyRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                playerSymbol = "O"
                opponentSymbol = "X"
                myTurn = false
                lobbyRef.child("playerO").setValue(true)
            } else {
                playerSymbol = "X"
                opponentSymbol = "O"
                myTurn = true
                val board = List(9) {""}
                val lobbyData = mapOf(
                    "board" to board,
                    "turn" to "X",
                    "playerX" to true,
                    "playerO" to false
                )
                lobbyRef.setValue(lobbyData)
            }

            listenForBoardUpdates()
        }
    }

    private fun listenForBoardUpdates(){
        val lobbyRef = database.child("lobbies").child(lobbyCode)

        lobbyRef.child("board").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val board = snapshot.children.map { it.getValue(String::class.java) ?: ""}
                updateBoardUI(board)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        lobbyRef.child("turn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val turn = snapshot.getValue(String::class.java)
                myTurn = (turn == playerSymbol)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun makeMove(index: Int){
        val cell = cells[index]
        if (cell.tag != null) return

        val lobbyRef = database.child("lobbies").child(lobbyCode)

        lobbyRef.child("board").child(index.toString()).setValue(playerSymbol)
        lobbyRef.child("turn").setValue(opponentSymbol)
    }

    private fun updateBoardUI(board: List<String>){
        for (i in board.indices){
            val value = board[i]
            val cell = cells[i]

            when (value){
                "X" -> {
                    cell.setImageResource(R.drawable.ic_pokemon3)
                    cell.tag = "X"
                }
                "O" -> {
                    cell.setImageResource(R.drawable.ic_pokemon1)
                    cell.tag = "O"
                }
                else -> {
                    cell.setImageDrawable(null)
                    cell.tag = null
                }
            }
        }
    }

    private fun generateLobbyCode(): String {
        return UUID.randomUUID().toString().take(6).uppercase(Locale.ROOT)
    }
}