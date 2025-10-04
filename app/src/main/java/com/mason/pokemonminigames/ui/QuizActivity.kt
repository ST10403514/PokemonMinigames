package com.mason.pokemonminigames.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.content.Intent
import com.mason.pokemonminigames.R

class QuizActivity : AppCompatActivity() {
    private val questions = listOf(
        Question("What type is Pikachu?", listOf("Fire", "Water", "Electric", "Grass"), 2),
        Question("Who is Ash’s first Pokémon?", listOf("Bulbasaur", "Pikachu", "Charmander", "Squirtle"), 1),
        Question("What does a Poké Ball do?", listOf("Heals Pokémon", "Catches Pokémon", "Feeds Pokémon", "Levels up Pokémon"), 1)
    )

    private var currentQuestion = 0
    private var score = 0

    private lateinit var tvQuestion: TextView
    private lateinit var rgOptions: RadioGroup
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        tvQuestion = findViewById(R.id.tvQuestion)
        rgOptions = findViewById(R.id.rgOptions)
        btnNext = findViewById(R.id.btnNext)

        loadQuestion()

        btnNext.setOnClickListener {
            val selected = rgOptions.checkedRadioButtonId
            if (selected != -1) {
                val answerIndex = rgOptions.indexOfChild(findViewById(selected))
                if (answerIndex == questions[currentQuestion].correctIndex) score++

                currentQuestion++
                if (currentQuestion < questions.size) {
                    loadQuestion()
                } else {
                    finishQuiz()
                }
            } else {
                Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadQuestion() {
        val q = questions[currentQuestion]
        tvQuestion.text = q.text
        rgOptions.removeAllViews()
        q.options.forEach {
            val rb = RadioButton(this)
            rb.text = it
            rgOptions.addView(rb)
        }
    }

    private fun finishQuiz() {
        val intent = Intent()
        intent.putExtra("quiz_score", score)
        setResult(RESULT_OK, intent)
        finish()
    }

    data class Question(val text: String, val options: List<String>, val correctIndex: Int)
}