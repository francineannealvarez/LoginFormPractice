package com.example.loginformpractice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // <-- use your XML layout here

        val usernameEditText = findViewById<EditText>(R.id.editTextText5)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.button2)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty()) {
                usernameEditText.error = "Username is required"
                usernameEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            // Continue with login logic
        }
    }
}
