package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.notes_show.*

class NotesShow: AppCompatActivity() {
    lateinit var book: Book
    companion object {
        val BOOK_INTENT = "BOOK"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_show)
        book = intent.getSerializableExtra("BOOK") as Book
        var arr = DbHelper(this).getNotes(book.id)
        var notes_str = ""
        var iter = arr.size - 1
        for(i in 0..iter){
            notes_str += arr[i] + System.lineSeparator()
        }
        showNotes.setText(notes_str)
    }
}