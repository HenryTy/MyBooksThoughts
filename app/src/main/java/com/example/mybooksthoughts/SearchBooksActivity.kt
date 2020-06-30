package com.example.mybooksthoughts

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.books.Books
import com.google.api.services.books.BooksRequestInitializer
import com.google.api.services.books.model.Volumes
import kotlinx.android.synthetic.main.activity_search_books.*


class SearchBooksActivity : AppCompatActivity(), DownloadCallback<Volumes> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_books)

        searchButton.setOnClickListener { searchBooks(searchEditText.text.toString()) }
    }

    private fun searchBooks(query: String) {
        val books =
            Books.Builder(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null)
                .setGoogleClientRequestInitializer(BooksRequestInitializer(Keys.API_KEY))
                .build()
        val volumesListReq = books.volumes().list(query)
        val downloadTask = DownloadTask<Volumes>(this)
        downloadTask.execute(volumesListReq)
    }

    override fun updateFromDownload(result: Volumes?) {
        if(result != null && result.totalItems > 0 && result.items != null) {
            val bookList = mutableListOf<Book>()
            for (volume in result.items) {
                bookList.add(Book.createFromVolume(volume))
            }
            with(booksRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = BooksAdapter(bookList, this@SearchBooksActivity)
            }
        }
    }

    override fun getActiveNetworkInfo(): NetworkInfo {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }
}
