package com.example.mybooksthoughts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.services.books.BooksScopes
import kotlinx.android.synthetic.main.activity_book_details.*

class BookDetailsActivity : AppCompatActivity() {

    lateinit var book: Book
    var isRead = false

    private val RC_REQUEST_PERMISSION_BOOKS = 1
    private lateinit var googleAccount: GoogleSignInAccount

    companion object {
        val BOOK_INTENT = "BOOK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        book = intent.getSerializableExtra("BOOK") as Book
        titleTextView.text = book.title

        val googleAccountIfSigned = GoogleSignIn.getLastSignedInAccount(this)
        if(googleAccountIfSigned != null) {
            googleAccount = googleAccountIfSigned
            checkIfIsRead()
        }
        else {
            changeReadStatusButton.visibility = View.GONE
        }

        changeReadStatusButton.setOnClickListener { changeReadStatusOrAskForPermission() }
    }

    private fun checkIfIsRead() {
        if (GoogleSignIn.hasPermissions(
                googleAccount,
                Scope(BooksScopes.BOOKS)
            )) {
            ReadBooksManager.isBookOnReadList(book, this, googleAccount) {
                    isRead -> this.isRead = isRead
                    if(isRead) {
                        showRemoveFromReadButton()
                    }
            }
        }
    }

    private fun showAddToReadButton() {
        changeReadStatusButton.setText(R.string.add_to_read)
    }

    private fun showRemoveFromReadButton() {
        changeReadStatusButton.setText(R.string.remove_from_read)
    }

    private fun changeReadStatusOrAskForPermission() {
        if (!GoogleSignIn.hasPermissions(
                googleAccount,
                Scope(BooksScopes.BOOKS)
            )) {
            GoogleSignIn.requestPermissions(
                this,
                RC_REQUEST_PERMISSION_BOOKS,
                googleAccount,
                Scope(BooksScopes.BOOKS)
            )
        } else {
            changeReadStatus()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (RC_REQUEST_PERMISSION_BOOKS == requestCode) {
                changeReadStatus()
            }
        }
    }

    private fun changeReadStatus() {
        if(isRead) {
            ReadBooksManager.removeBookFromRead(book,this, googleAccount) {
                Toast.makeText(this, R.string.removed, Toast.LENGTH_SHORT).show()
                showAddToReadButton()
                isRead = false
            }
        }
        else {
            ReadBooksManager.addBookToRead(book,this, googleAccount) {
                Toast.makeText(this, R.string.added, Toast.LENGTH_SHORT).show()
                showRemoveFromReadButton()
                isRead = true
            }
        }
    }
}
