package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.description_show.*


class DescriptionShow: AppCompatActivity() {
    lateinit var book: Book
    companion object {
        val BOOK_INTENT = "BOOK"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.description_show)
        book = intent.getSerializableExtra("BOOK") as Book
        descText.setText(book.description)
    }
}