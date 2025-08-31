package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mason.pokemonminigames.databinding.ActivityHomeBinding
import com.mason.pokemonminigames.util.LocaleHelper
import com.mason.pokemonminigames.util.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun attachBaseContext(newBase: android.content.Context?) {
        if (newBase == null) { super.attachBaseContext(newBase); return }
        val sm = SessionManager(newBase)
        val ctx = LocaleHelper.applyLocale(newBase, sm.getLanguage())
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
