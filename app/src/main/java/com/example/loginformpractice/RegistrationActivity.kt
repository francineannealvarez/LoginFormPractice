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

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val fullNameEditText = findViewById<EditText>(R.id.fullNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmpasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            var hasError = false

            if (fullName.isEmpty()) {
                fullNameEditText.error = "Full Name is required"
                hasError = true
            }

            if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                hasError = true
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                hasError = true
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "Confirm your password"
                hasError = true
            }

            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                confirmPasswordEditText.error = "Passwords do not match"
                hasError = true
            }

            if (hasError) {
                // Optional: focus the first field with error
                if (fullName.isEmpty()) fullNameEditText.requestFocus()
                else if (email.isEmpty()) emailEditText.requestFocus()
                else if (password.isEmpty()) passwordEditText.requestFocus()
                else confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            // All validations passed
            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }, 1500)
        }
    }
}
