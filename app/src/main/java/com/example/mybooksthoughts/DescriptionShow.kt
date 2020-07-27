package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.description_show.*


class DescriptionShow: AppCompatActivity() {
    lateinit var book: Book
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.description_show)
        book = intent.getSerializableExtra(BookDetailsActivity.BOOK_INTENT) as Book
        if(book.description != null) {
            descText.setText(book.description)
        }
    }
}