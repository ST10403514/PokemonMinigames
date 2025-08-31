package com.mason.pokemonminigames.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mason.pokemonminigames.databinding.ActivityRegisterBinding
import com.mason.pokemonminigames.util.BiometricHelper
import com.mason.pokemonminigames.util.LocaleHelper
import com.mason.pokemonminigames.util.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        auth = FirebaseAuth.getInstance()

        binding.btnCreate.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val confirm = binding.etConfirm.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val enableBio = binding.cbEnableBiometric.isChecked
                    session.setBiometricEnabled(enableBio)

                    if (enableBio) {
                        val bio = BiometricHelper(this)
                        if (bio.canAuth()) { // ✅ fixed
                            bio.authenticate(
                                onSuccess = { goHome() },
                                onError = { goHome() } // fallback
                            )
                        } else {
                            goHome()
                        }
                    } else {
                        goHome()
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
