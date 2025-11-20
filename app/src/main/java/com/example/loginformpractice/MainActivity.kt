package com.example.loginformpractice

import android.text.Html
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signUpText: TextView = findViewById(R.id.signUpText)
        signUpText.text = "Register Here"
        signUpText.paintFlags = signUpText.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        signUpText.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
