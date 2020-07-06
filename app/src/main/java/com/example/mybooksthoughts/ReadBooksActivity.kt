package com.example.mybooksthoughts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.services.books.BooksScopes
import kotlinx.android.synthetic.main.activity_read_books.*


class ReadBooksActivity : AppCompatActivity() {

    private val RC_REQUEST_PERMISSION_BOOKS = 1
    private lateinit var googleAccount: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_books)
    }

    override fun onStart() {
        super.onStart()
        val googleAccountIfSigned = GoogleSignIn.getLastSignedInAccount(this)

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
        ReadBooksManager.requestReadBooks(this, googleAccount) {books ->
            showReadBooks(books)
        }
    }

    private fun showReadBooks(bookList: List<Book>) {
        with(booksRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = BooksAdapter(bookList, this@ReadBooksActivity)
        }
    }
}
