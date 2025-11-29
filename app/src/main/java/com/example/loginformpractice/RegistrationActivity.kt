package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val ageEditText = findViewById<EditText>(R.id.ageEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmpasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(fullName, age, email, password, confirmPassword, fullNameEditText, ageEditText, emailEditText, passwordEditText, confirmPasswordEditText)) {
                // All local validations passed, proceed with Firebase registration
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, now save the user data to Firestore
                            val firebaseUser = auth.currentUser
                            val user = hashMapOf(
                                "fullName" to fullName,
                                "age" to age,
                                "email" to email
                            )

                            firebaseUser?.let {
                                db.collection("users").document(it.uid)
                                    .set(user)
                                    .addOnSuccessListener { 
                                        // Show success message and navigate
                                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }, 1500)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun validateInput(fullName: String, age: String, email: String, pass: String, confPass: String,
                              fullNameET: EditText, ageET: EditText, emailET: EditText, passET: EditText, confPassET: EditText): Boolean {
        var isValid = true
        if (fullName.isEmpty()) {
            fullNameET.error = "Full Name is required"
            isValid = false
        }
        if (age.isEmpty()) {
            ageET.error = "Age is required"
            isValid = false
        }
        if (email.isEmpty()) {
            emailET.error = "Email is required"
            isValid = false
        }
        if (pass.isEmpty()) {
            passET.error = "Password is required"
            isValid = false
        }
        if (confPass.isEmpty()) {
            confPassET.error = "Confirm your password"
            isValid = false
        }
        if (pass.isNotEmpty() && confPass.isNotEmpty() && pass != confPass) {
            confPassET.error = "Passwords do not match"
            isValid = false
        }
        return isValid
    }
}
