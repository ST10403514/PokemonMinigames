package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mason.pokemonminigames.databinding.ActivityLoginBinding
import com.mason.pokemonminigames.util.BiometricHelper
import com.mason.pokemonminigames.util.LocaleHelper
import com.mason.pokemonminigames.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var session: SessionManager

    override fun attachBaseContext(newBase: android.content.Context?) {
        if (newBase == null) { super.attachBaseContext(newBase); return }
        val sm = SessionManager(newBase)
        val ctx = LocaleHelper.applyLocale(newBase, sm.getLanguage())
        super.attachBaseContext(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        auth = FirebaseAuth.getInstance()

        binding.btnEmailLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email & password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val enableBio = binding.cbEnableBiometric.isChecked
                    session.setBiometricEnabled(enableBio)

                    if (enableBio) {
                        val bio = BiometricHelper(this)
                        if (bio.canAuth()) { // ✅ updated here
                            bio.authenticate(
                                onSuccess = { goHome() },
                                onError = { goHome() } // fallback to proceed
                            )
                        } else {
                            goHome()
                        }
                    } else {
                        goHome()
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
