package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if we should show logout notification
        val showLogoutSuccess = intent.getBooleanExtra("SHOW_LOGOUT_SUCCESS", false)
        if (showLogoutSuccess) {
            Handler(Looper.getMainLooper()).postDelayed({
                NotificationHelper.showInfoNotification(
                    this,
                    "Logged out successfully. See you soon!"
                )
            }, 500)
        }

        if (auth.currentUser != null) {
            navigateToHome()
        }

        val emailEditText = findViewById<EditText>(R.id.editTextText5)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.button2)
        val signUpText: TextView = findViewById(R.id.signUpText)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                NotificationHelper.showErrorNotification(this, "Please enter email and password")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get user's name from Firestore
                        val user = auth.currentUser
                        user?.let {
                            db.collection("users").document(it.uid).get()
                                .addOnSuccessListener { document ->
                                    val fullName = document.getString("fullName") ?: "User"
                                    val firstName = fullName.split(" ").first()

                                    // Navigate to home and pass the name
                                    navigateToHome(firstName)
                                }
                                .addOnFailureListener {
                                    // Navigate to home without name
                                    navigateToHome()
                                }
                        }
                    } else {
                        NotificationHelper.showErrorNotification(
                            this,
                            "Login failed: ${task.exception?.message}"
                        )
                    }
                }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToHome(userName: String? = null) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        userName?.let {
            intent.putExtra("SHOW_LOGIN_SUCCESS", true)
            intent.putExtra("USER_NAME", it)
        }
        startActivity(intent)
    }
}