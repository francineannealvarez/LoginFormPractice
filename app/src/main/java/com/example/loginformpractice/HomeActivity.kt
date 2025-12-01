package com.example.loginformpractice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val welcomeMessageTextView = findViewById<TextView>(R.id.welcomeMessage)
        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)
        val quoteTextView = findViewById<TextView>(R.id.quoteTextView)

        val moodAngry = findViewById<LinearLayout>(R.id.moodAngry)
        val moodSad = findViewById<LinearLayout>(R.id.moodSad)
        val moodNeutral = findViewById<LinearLayout>(R.id.moodNeutral)
        val moodHappy = findViewById<LinearLayout>(R.id.moodHappy)
        val moodVeryHappy = findViewById<LinearLayout>(R.id.moodVeryHappy)

        // Check if we should show login success notification
        val showLoginSuccess = intent.getBooleanExtra("SHOW_LOGIN_SUCCESS", false)
        val showRegistrationSuccess = intent.getBooleanExtra("SHOW_REGISTRATION_SUCCESS", false)
        val userName = intent.getStringExtra("USER_NAME")

        if (showLoginSuccess && userName != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                NotificationHelper.showSuccessNotification(
                    this,
                    "Successfully logged in as $userName!"
                )
            }, 500)
        }

        if (showRegistrationSuccess && userName != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                NotificationHelper.showSuccessNotification(
                    this,
                    "Account created successfully! Welcome, $userName!"
                )
            }, 500)
        }

        val user = auth.currentUser
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName")
                        if (!fullName.isNullOrEmpty()) {
                            val firstName = fullName.split(" ").first()
                            welcomeMessageTextView.text = getString(R.string.welcome_with_name, firstName)
                        } else {
                            welcomeMessageTextView.text = getString(R.string.welcome_default)
                        }
                    } else {
                        welcomeMessageTextView.text = getString(R.string.welcome_default)
                        Toast.makeText(this, R.string.user_data_not_found, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, getString(R.string.error_getting_user_details, exception.message), Toast.LENGTH_SHORT).show()
                    welcomeMessageTextView.text = getString(R.string.welcome_default)
                }
        } else {
            navigateToLogin()
        }

        logoutIcon.setOnClickListener {
            showLogoutConfirmation()
        }

        moodAngry.setOnClickListener {
            quoteTextView.text = getString(R.string.quote_angry)
            quoteTextView.visibility = View.VISIBLE
        }

        moodSad.setOnClickListener {
            quoteTextView.text = getString(R.string.quote_sad)
            quoteTextView.visibility = View.VISIBLE
        }

        moodNeutral.setOnClickListener {
            quoteTextView.text = getString(R.string.quote_neutral)
            quoteTextView.visibility = View.VISIBLE
        }

        moodHappy.setOnClickListener {
            quoteTextView.text = getString(R.string.quote_happy)
            quoteTextView.visibility = View.VISIBLE
        }

        moodVeryHappy.setOnClickListener {
            quoteTextView.text = getString(R.string.quote_very_happy)
            quoteTextView.visibility = View.VISIBLE
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                auth.signOut()

                // Navigate to login and show notification there
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("SHOW_LOGOUT_SUCCESS", true)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}