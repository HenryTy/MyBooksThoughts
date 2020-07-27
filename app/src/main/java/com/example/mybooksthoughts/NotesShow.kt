package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.notes_show.*

class NotesShow: AppCompatActivity() {
    lateinit var book: Book
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_show)
        book = intent.getSerializableExtra(BookDetailsActivity.BOOK_INTENT) as Book
        var arr = DbHelper(this).getNotes(book.id)

        with(notesRecycler) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = NotesAdapter(arr)
        }
    }
}