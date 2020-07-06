package com.example.mybooksthoughts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_item.view.*

class BooksAdapter(
    private val booksList: List<Book>,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.mView
        val book = booksList[position]
        if(book.title != null) {
            itemView.titleTextView.text = book.title
        }
        else {
            itemView.titleTextView.text = ""
        }
        itemView.authorTextView.text = book.getAuthorsText()
        if(book.imageUrl != null) {
            val downloadImageTask = DownloadImageTask(itemView.coverImageView)
            downloadImageTask.execute(book.imageUrl)
        }
        itemView.setOnClickListener { showBookDetails(book) }
    }

    override fun getItemCount(): Int = booksList.size

    private fun showBookDetails(book: Book) {
        val intent = Intent(activity, BookDetailsActivity::class.java)
        intent.putExtra(BookDetailsActivity.BOOK_INTENT, book)
        activity.startActivity(intent)
    }
}