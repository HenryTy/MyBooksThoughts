package com.example.mybooksthoughts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note.view.*

class NotesAdapter(private val notesList: List<String>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mView.noteText.text = notesList[position]
    }

    override fun getItemCount(): Int = notesList.size
}