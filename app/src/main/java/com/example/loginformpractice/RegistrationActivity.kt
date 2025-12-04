package com.example.loginformpractice

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Apply gradient to LUNA text
        applyGradientToLunaText()

        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val ageEditText = findViewById<EditText>(R.id.ageEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmpasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)
        val loginText: TextView = findViewById(R.id.loginText)

        loginText.paintFlags = loginText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString() // Don't trim password
            val confirmPassword = confirmPasswordEditText.text.toString() // Don't trim password

            // Clear previous errors
            fullNameEditText.error = null
            ageEditText.error = null
            emailEditText.error = null
            passwordEditText.error = null
            confirmPasswordEditText.error = null

            if (validateInput(fullName, age, email, password, confirmPassword,
                    fullNameEditText, ageEditText, emailEditText, passwordEditText, confirmPasswordEditText)) {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
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
                                        // Navigate to home and show notification there
                                        val firstName = fullName.split(" ").first()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("SHOW_REGISTRATION_SUCCESS", true)
                                        intent.putExtra("USER_NAME", firstName)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        NotificationHelper.showErrorNotification(
                                            this,
                                            "Failed to save user data. Please try again."
                                        )
                                    }
                            }
                        } else {
                            // Handle specific registration errors
                            val errorMessage = when (task.exception) {
                                is FirebaseAuthUserCollisionException -> {
                                    "This email is already registered. Please log in instead."
                                }
                                else -> {
                                    "Registration failed. Please try again."
                                }
                            }
                            NotificationHelper.showErrorNotification(this, errorMessage)
                        }
                    }
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun applyGradientToLunaText() {
        val lunaTextView = findViewById<TextView>(R.id.appNameTitle)

        lunaTextView.post {
            // Vertical gradient (top to bottom)
            val textShader = LinearGradient(
                0f, 0f, 0f, lunaTextView.height.toFloat(),
                intArrayOf(
                    0xFF9FA8DA.toInt(),  // Light periwinkle (top)
                    0xFF4A148C.toInt()   // Dark purple (bottom)
                ),
                null,
                Shader.TileMode.CLAMP
            )

            lunaTextView.paint.shader = textShader
            lunaTextView.invalidate()
        }
    }

    private fun validateInput(
        fullName: String, age: String, email: String, pass: String, confPass: String,
        fullNameET: EditText, ageET: EditText, emailET: EditText, passET: EditText, confPassET: EditText
    ): Boolean {

        // Validate Full Name
        if (fullName.isEmpty()) {
            fullNameET.error = "Please enter your full name"
            NotificationHelper.showErrorNotification(this, "Please enter your full name")
            return false
        }

        // Validate Age
        if (age.isEmpty()) {
            ageET.error = "Please enter your age"
            NotificationHelper.showErrorNotification(this, "Please enter your age")
            return false
        }

        val ageInt = age.toIntOrNull()
        if (ageInt == null || ageInt < 1 || ageInt > 120) {
            ageET.error = "Please enter a valid age"
            NotificationHelper.showErrorNotification(this, "Please enter a valid age")
            return false
        }

        // Validate Email
        if (email.isEmpty()) {
            emailET.error = "Please enter your email"
            NotificationHelper.showErrorNotification(this, "Please enter your email")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.error = "Invalid email format"
            NotificationHelper.showErrorNotification(this, "Please enter a valid email address")
            return false
        }

        // Validate Password
        if (pass.isEmpty()) {
            passET.error = "Please create a password"
            NotificationHelper.showErrorNotification(this, "Please create a password")
            return false
        }

        // Check password length
        if (pass.length < 8) {
            passET.error = "At least 8 characters"
            NotificationHelper.showErrorNotification(this, "Password must be at least 8 characters long")
            return false
        }

        // Check for spaces in password
        if (pass.contains(" ")) {
            passET.error = "No spaces allowed"
            NotificationHelper.showErrorNotification(this, "Password cannot contain spaces")
            return false
        }

        // Check for letters and numbers
        val hasLetter = pass.any { it.isLetter() }
        val hasDigit = pass.any { it.isDigit() }

        if (!hasLetter || !hasDigit) {
            passET.error = "Must contain letters and numbers"
            NotificationHelper.showErrorNotification(this, "Password must contain both letters and numbers")
            return false
        }

        // Validate Confirm Password
        if (confPass.isEmpty()) {
            confPassET.error = "Please confirm your password"
            NotificationHelper.showErrorNotification(this, "Please confirm your password")
            return false
        }

        // Check if passwords match
        if (pass != confPass) {
            confPassET.error = "Passwords do not match"
            NotificationHelper.showErrorNotification(this, "Passwords do not match. Please try again.")
            return false
        }

        return true
    }
}