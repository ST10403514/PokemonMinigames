package com.mason.pokemonminigames.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.databinding.ActivityMainBinding
import com.mason.pokemonminigames.util.BiometricHelper
import com.mason.pokemonminigames.util.LocaleHelper
import com.mason.pokemonminigames.util.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var session: SessionManager

    // Google Sign-In launcher
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { res ->
                if (res.isSuccessful) goHome()
            }
        }
    }

    // 👇 Apply stored locale to this Activity
    @Suppress("DEPRECATION")
    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(newBase)
            return
        }
        val sm = SessionManager(newBase)
        val ctx = LocaleHelper.applyLocale(newBase, sm.getLanguage())
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // ⚡ Switch from splash to main theme
        setTheme(R.style.Theme_PokemonMinigames)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        auth = FirebaseAuth.getInstance()

        setupLanguageSpinner()
        setupButtons()
        setupGoogleSignIn()
        checkBiometricLogin()
    }

    private fun setupLanguageSpinner() {
        val langs = listOf("English" to "en", "Zulu" to "zu", "Afrikaans" to "af")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            langs.map { it.first }
        )
        binding.spLanguage.adapter = adapter

        // Select currently saved language
        val currentCode = session.getLanguage()
        binding.spLanguage.setSelection(langs.indexOfFirst { it.second == currentCode }.coerceAtLeast(0))

        // On change → update session + refresh
        binding.spLanguage.setOnItemSelectedListenerCompat { position ->
            val code = langs[position].second
            if (code != session.getLanguage()) {
                session.setLanguage(code)
                recreate() // refresh UI immediately
            }
        }
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogle.setOnClickListener {
            googleSignInLauncher.launch(googleClient.signInIntent)
        }
    }

    private fun checkBiometricLogin() {
        val current = auth.currentUser
        if (current != null && session.isBiometricEnabled()) {
            val bio = BiometricHelper(this)
            if (bio.canAuth()) {
                bio.authenticate(
                    onSuccess = { goHome() },
                    onError = { /* stay on main */ }
                )
            } else {
                goHome()
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

// Spinner extension for cleaner listener setup
private fun android.widget.Spinner.setOnItemSelectedListenerCompat(onSelected: (Int) -> Unit) {
    this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: android.widget.AdapterView<*>,
            view: android.view.View?,
            position: Int,
            id: Long
        ) {
            onSelected(position)
        }

        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
    }
}
