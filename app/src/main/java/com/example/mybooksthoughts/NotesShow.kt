package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.notes_show.*

class NotesShow: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes_show)
        var arr = DbHelper(this).getNotes(BookDetailsActivity().book.id)
        var notes_str = ""
        for(i in 0..arr.size){
            notes_str += arr[i] + "/n"
        }
        showNotes.setText(notes_str)
    }
}