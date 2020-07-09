package com.example.mybooksthoughts

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.books.Books
import com.google.api.services.books.BooksRequest

object ReadBooksManager {

    fun isBookOnReadList(book: Book, context: Context,
                         googleAccount: GoogleSignInAccount,
                         callback: (Boolean) -> (Unit)) {
        requestReadBooks(context, googleAccount) {books ->
            if(books != null) {
                for (b in books) {
                    if (b.id == book.id) {
                        callback.invoke(true)
                        return@requestReadBooks
                    }
                }
            }
            callback.invoke(false)
        }
    }

    fun requestReadBooks(context: Context, googleAccount: GoogleSignInAccount, callback: (List<Book>?) -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().volumes().list("4")},
            {result ->
                if(result == null) {
                    callback.invoke(null)
                    return@makeRequest
                }
                val bookList = mutableListOf<Book>()
                if(result.totalItems > 0 && result.items != null) {
                    for(volume in result.items) {
                        bookList.add(Book.createFromVolume(volume))
                    }
                }
                callback.invoke(bookList)
            }, false)
    }

    fun addBookToRead(book: Book, context: Context,
                      googleAccount: GoogleSignInAccount,
                      callback: () -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().addVolume("4", book.id)},
            { callback.invoke() }, true)
    }

    fun removeBookFromRead(book: Book, context: Context,
                      googleAccount: GoogleSignInAccount,
                      callback: () -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().removeVolume("4", book.id)},
            { callback.invoke() }, true)
    }

    private fun <T> makeRequest(context: Context,
                                googleAccount: GoogleSignInAccount,
                                requestCreator: (Books) -> (BooksRequest<T>),
                                resultCallback: (T?) -> (Unit),
                                showNoConnectionMsg: Boolean) {
        TokenManager.getToken(context, googleAccount) { accessToken ->
            makeRequestWithAccessToken(context, accessToken, requestCreator,
                resultCallback, showNoConnectionMsg) }
    }

    private fun <T> makeRequestWithAccessToken(context: Context,
                                               accessToken: String?,
                                               requestCreator: (Books) -> (BooksRequest<T>),
                                               resultCallback: (T?) -> (Unit),
                                               showNoConnectionMsg: Boolean) {
        if(accessToken == null) {
            if(showNoConnectionMsg) {
                Toast.makeText(context, R.string.no_connection_msg, Toast.LENGTH_SHORT).show()
            }
            else {
                resultCallback.invoke(null)
            }
            return
        }
        val books: Books = Books.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                GoogleCredential().setAccessToken(accessToken))
            .build()
        val req = requestCreator.invoke(books)
        val downloadTask = DownloadTask(context, object : DownloadCallback<T> {
            override fun updateFromDownload(result: T?) {
                resultCallback.invoke(result)
            }

            override fun onNoConnection() {
                if(showNoConnectionMsg) {
                    Toast.makeText(context, R.string.no_connection_msg, Toast.LENGTH_SHORT).show()
                }
                else {
                    resultCallback.invoke(null)
                }
            }
        })
        downloadTask.execute(req)
    }
}