package com.example.loginformpractice

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt

object NotificationHelper {

    fun showSuccessNotification(activity: Activity, message: String) {
        showCustomNotification(activity, message, "#4CAF50", R.drawable.ic_check_circle)
    }

    fun showErrorNotification(activity: Activity, message: String) {
        showCustomNotification(activity, message, "#F44336", R.drawable.ic_error)
    }

    fun showInfoNotification(activity: Activity, message: String) {
        showCustomNotification(activity, message, "#2196F3", R.drawable.ic_info)
    }

    private fun showCustomNotification(activity: Activity, message: String, colorHex: String, iconRes: Int) {
        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val inflater = LayoutInflater.from(activity)
        val customView = inflater.inflate(R.layout.custom_notification, rootView, false)

        // Set message and icon
        customView.findViewById<TextView>(R.id.notificationMessage).text = message
        customView.findViewById<ImageView>(R.id.notificationIcon).setImageResource(iconRes)

        // Set background color
        val cardView = customView.findViewById<CardView>(R.id.notificationCard)
        cardView.setCardBackgroundColor(colorHex.toColorInt())

        // Create layout params for top positioning (below status bar and camera)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP
            topMargin = 120
            leftMargin = 16
            rightMargin = 16
        }

        customView.layoutParams = layoutParams
        customView.alpha = 0f
        customView.translationY = -100f

        // Add to root view
        rootView.addView(customView)

        // Animate in
        customView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()

        // Remove after 3 seconds
        customView.postDelayed({
            customView.animate()
                .alpha(0f)
                .translationY(-100f)
                .setDuration(300)
                .withEndAction {
                    rootView.removeView(customView)
                }
                .start()
        }, 3000)
    }
}