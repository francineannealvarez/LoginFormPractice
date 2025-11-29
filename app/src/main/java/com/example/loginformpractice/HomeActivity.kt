package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val welcomeMessageTextView = findViewById<TextView>(R.id.welcomeMessage)
        val settingsIcon = findViewById<ImageView>(R.id.settingsIcon)

        // Fetch and display user's name
        val user = auth.currentUser
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName")
                        if (!fullName.isNullOrEmpty()) {
                            val firstName = fullName.split(" ").first()
                            welcomeMessageTextView.text = "Welcome to Luna, $firstName!"
                        } else {
                            welcomeMessageTextView.text = "Welcome to Luna!"
                        }
                    } else {
                        // This case can happen if auth is created but firestore write fails
                        welcomeMessageTextView.text = "Welcome to Luna!"
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting user details: ${exception.message}", Toast.LENGTH_SHORT).show()
                    welcomeMessageTextView.text = "Welcome to Luna!"
                }
        } else {
            // If for some reason the user is null, navigate back to login
            navigateToLogin()
        }

        // Set up logout functionality
        settingsIcon.setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
