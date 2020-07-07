package com.example.mybooksthoughts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.books.Books
import com.google.api.services.books.BooksRequestInitializer
import com.google.api.services.books.model.Volumes
import kotlinx.android.synthetic.main.activity_search_books.*


class SearchBooksActivity : AppCompatActivity(), DownloadCallback<Volumes> {

    companion object {
        val STATE_INDEX = "index"
        val STATE_QUERY = "query"
    }

    var currentBookStartIndex: Long = 0
    var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_books)

        booksRecycler.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val newQuery = searchEditText.text.toString()
            if(newQuery.trim() != "") {
                currentQuery = newQuery
                currentBookStartIndex = 0
                searchBooks()
            }
        }

        prevPageButton.setOnClickListener {
            if(currentBookStartIndex >= 10) {
                currentBookStartIndex -= 10
                searchBooks()
            }
        }

        nextPageButton.setOnClickListener {
            currentBookStartIndex += 10
            searchBooks()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putLong(STATE_INDEX, currentBookStartIndex)
            putString(STATE_QUERY, currentQuery)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            currentBookStartIndex = getLong(STATE_INDEX)
            currentQuery = getString(STATE_QUERY) ?: ""
        }
        if(currentQuery != "") {
            searchBooks()
        }
    }

    private fun searchBooks() {
        showProgressBar()
        val books =
            Books.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null)
                .setGoogleClientRequestInitializer(BooksRequestInitializer(Keys.API_KEY))
                .build()
        val volumesListReq = books.volumes().list(currentQuery).setStartIndex(currentBookStartIndex)
        val downloadTask = DownloadTask(this)
        downloadTask.execute(volumesListReq)
    }

    override fun updateFromDownload(result: Volumes?) {
        if(result != null && result.totalItems > 0 && result.items != null) {
            val bookList = mutableListOf<Book>()
            for (volume in result.items) {
                bookList.add(Book.createFromVolume(volume))
            }
            with(booksRecycler) {
                adapter = BooksAdapter(bookList, this@SearchBooksActivity)
            }
            setPagesButtonsVisibility(result.totalItems)
        }
    }

    private fun showProgressBar() {
        progressView.visibility = View.VISIBLE
        prevPageButton.visibility = View.INVISIBLE
        nextPageButton.visibility = View.INVISIBLE
    }

    private fun setPagesButtonsVisibility(totalItems: Int) {
        progressView.visibility = View.GONE
        if(currentBookStartIndex == 0L) {
            prevPageButton.visibility = View.INVISIBLE
        }
        else {
            prevPageButton.visibility = View.VISIBLE
        }
        if(currentBookStartIndex + 10 >= totalItems) {
            nextPageButton.visibility = View.INVISIBLE
        }
        else {
            nextPageButton.visibility = View.VISIBLE
        }
    }
}
