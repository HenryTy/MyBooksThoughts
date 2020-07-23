package com.example.mybooksthoughts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.transition.Fade
import android.view.Window
import androidx.core.app.ActivityOptionsCompat
import kotlinx.android.synthetic.main.menu_activity.*


class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableTransition()
        setContentView(R.layout.menu_activity)
        signOutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }

        readBooksButton.setOnClickListener {
            val intent = Intent(this, ReadBooksActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }

        searchBooksButton.setOnClickListener {
            val intent = Intent(this, SearchBooksActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    private fun enableTransition() {
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()
            exitTransition.duration = 1000
        }
    }
}