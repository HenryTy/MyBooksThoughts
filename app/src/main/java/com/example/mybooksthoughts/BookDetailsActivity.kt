package com.example.mybooksthoughts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_book_details.*

class BookDetailsActivity : AppCompatActivity() {

    companion object {
        val BOOK_INTENT = "BOOK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val book: Book = intent.getSerializableExtra("BOOK") as Book
        titleTextView.text = book.title
    }
}
