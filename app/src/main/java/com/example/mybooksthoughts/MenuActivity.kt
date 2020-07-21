package com.example.mybooksthoughts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import kotlinx.android.synthetic.main.menu_activity.*


class MenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        signOutButton.setOnClickListener { signOut() }
        readBooksButton.setOnClickListener {
            val intent = Intent(this, ReadBooksActivity::class.java)
            startActivity(intent)
        }

        searchBooksButton.setOnClickListener {
            val intent = Intent(this, SearchBooksActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signOut() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}