package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.loginformpractice.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupUI()
        checkIfUserIsLoggedIn()
        handleLogoutSuccess()
    }

    private fun setupUI() {
        binding.loginButton.setOnClickListener { handleLogin() }
        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (!isInputValid(email, password)) return

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchUserAndNavigateToHome()
                } else {
                    NotificationHelper.showErrorNotification(
                        this,
                        "Incorrect email or password. Please try again."
                    )
                }
            }
    }

    private fun fetchUserAndNavigateToHome() {
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val fullName = document.getString("fullName") ?: "User"
                    navigateToHome(fullName.split(" ").first())
                }
                .addOnFailureListener {
                    navigateToHome()
                }
        }
    }

    private fun isInputValid(email: String, pass: String): Boolean {
        if (email.isEmpty() || pass.isEmpty()) {
            NotificationHelper.showErrorNotification(this, "Please enter email and password")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            NotificationHelper.showErrorNotification(this, "Please enter a valid email address")
            return false
        }
        return true
    }

    private fun checkIfUserIsLoggedIn() {
        if (auth.currentUser != null) {
            navigateToHome()
        }
    }

    private fun handleLogoutSuccess() {
        if (intent.getBooleanExtra("SHOW_LOGOUT_SUCCESS", false)) {
            Handler(Looper.getMainLooper()).postDelayed({
                NotificationHelper.showInfoNotification(
                    this,
                    "Logged out successfully. See you soon!"
                )
            }, 500)
        }
    }

    private fun navigateToHome(userName: String? = null) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            userName?.let {
                putExtra("SHOW_LOGIN_SUCCESS", true)
                putExtra("USER_NAME", it)
            }
        }
        startActivity(intent)
    }
}
