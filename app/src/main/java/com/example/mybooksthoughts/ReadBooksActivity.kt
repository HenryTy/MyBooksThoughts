package com.example.mybooksthoughts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.books.Books
import com.google.api.services.books.BooksScopes
import com.google.api.services.books.model.Volumes
import kotlinx.android.synthetic.main.activity_read_books.*


class ReadBooksActivity : AppCompatActivity(), DownloadCallback<Volumes> {

    private val RC_REQUEST_PERMISSION_BOOKS = 1
    private lateinit var googleAccount: GoogleSignInAccount
    private var booksToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_books)

        val googleAccountIfSigned = GoogleSignIn.getLastSignedInAccount(this)

        booksToken = TokenManager.getToken(this)

        if(googleAccountIfSigned != null) {
            googleAccount = googleAccountIfSigned
            if (!GoogleSignIn.hasPermissions(
                    googleAccount,
                    Scope(BooksScopes.BOOKS))) {
                GoogleSignIn.requestPermissions(
                    this,
                    RC_REQUEST_PERMISSION_BOOKS,
                    googleAccount,
                    Scope(BooksScopes.BOOKS))
            } else {
                requestReadBooks()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (RC_REQUEST_PERMISSION_BOOKS == requestCode) {
                requestReadBooks()
            }
        }
    }

    private fun requestReadBooks() {
        if(booksToken == null) {
            val getTokenTask =
                GetTokenTask { accessToken ->
                    TokenManager.saveToken(this, accessToken)
                    requestReadBooksWithAccessToken(accessToken) }
            getTokenTask.execute(googleAccount)
        }
        else {
            requestReadBooksWithAccessToken(booksToken)
        }
    }

    private fun requestReadBooksWithAccessToken(accessToken: String?) {
        val books: Books = Books.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                GoogleCredential().setAccessToken(accessToken))
            .build()
        val volumesListReq = books.mylibrary().bookshelves().volumes().list("4")
        val downloadTask = DownloadTask<Volumes>(this)
        downloadTask.execute(volumesListReq)
    }

    private fun showReadBooks(volumes: Volumes) {
        val bookList = mutableListOf<Book>()
        for(volume in volumes.items) {
            bookList.add(Book.createFromVolume(volume))
        }
        with(booksRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = BooksAdapter(bookList, this@ReadBooksActivity)
        }
    }

    override fun updateFromDownload(result: Volumes?) {
        if(result != null && result.totalItems > 0 && result.items != null) {
            showReadBooks(result)
        }
    }

    override fun getActiveNetworkInfo(): NetworkInfo {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }
}
