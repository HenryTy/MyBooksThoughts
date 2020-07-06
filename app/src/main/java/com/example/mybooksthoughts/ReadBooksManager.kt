package com.example.mybooksthoughts

import android.content.Context
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
            for(b in books) {
                if(b.id == book.id) {
                    callback.invoke(true)
                    return@requestReadBooks
                }
            }
            callback.invoke(false)
        }
    }

    fun requestReadBooks(context: Context, googleAccount: GoogleSignInAccount, callback: (List<Book>) -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().volumes().list("4")},
            {result ->
                if(result != null && result.totalItems > 0 && result.items != null) {
                    val bookList = mutableListOf<Book>()
                    for(volume in result.items) {
                        bookList.add(Book.createFromVolume(volume))
                    }
                    callback.invoke(bookList)
                }
            })
    }

    fun addBookToRead(book: Book, context: Context,
                      googleAccount: GoogleSignInAccount,
                      callback: () -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().addVolume("4", book.id)},
            { callback.invoke() })
    }

    fun removeBookFromRead(book: Book, context: Context,
                      googleAccount: GoogleSignInAccount,
                      callback: () -> (Unit)) {
        makeRequest(context, googleAccount,
            {books ->  books.mylibrary().bookshelves().removeVolume("4", book.id)},
            { callback.invoke() })
    }

    private fun <T> makeRequest(context: Context,
                                googleAccount: GoogleSignInAccount,
                                requestCreator: (Books) -> (BooksRequest<T>),
                                resultCallback: (T?) -> (Unit)) {
        TokenManager.getToken(context, googleAccount) { accessToken ->
            makeRequestWithAccessToken(accessToken, requestCreator, resultCallback) }
    }

    private fun <T> makeRequestWithAccessToken(accessToken: String?,
                                               requestCreator: (Books) -> (BooksRequest<T>),
                                               resultCallback: (T?) -> (Unit)) {
        val books: Books = Books.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                GoogleCredential().setAccessToken(accessToken))
            .build()
        val req = requestCreator.invoke(books)
        val downloadTask = DownloadTask(object : DownloadCallback<T> {
            override fun updateFromDownload(result: T?) {
                resultCallback.invoke(result)
            }
        })
        downloadTask.execute(req)
    }
}