package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validateInput(fullName, age, email, password, confirmPassword, fullNameEditText, ageEditText, emailEditText, passwordEditText, confirmPasswordEditText)) {
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
                                    .addOnFailureListener { e ->
                                        NotificationHelper.showErrorNotification(
                                            this,
                                            "Error: ${e.message}"
                                        )
                                    }
                            }
                        } else {
                            NotificationHelper.showErrorNotification(
                                this,
                                "Registration failed: ${task.exception?.message}"
                            )
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