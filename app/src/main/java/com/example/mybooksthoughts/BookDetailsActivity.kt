package com.example.mybooksthoughts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
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
        enableTransition()
        setContentView(R.layout.activity_book_details)
        var editText = findViewById<EditText>(R.id.newNote)
        book = intent.getSerializableExtra("BOOK") as Book
        val googleAccountIfSigned = GoogleSignIn.getLastSignedInAccount(this)
        if(googleAccountIfSigned != null) {
            googleAccount = googleAccountIfSigned
            checkIfIsRead()
        }
        else {
            changeReadStatusButton.visibility = View.GONE
        }

        changeReadStatusButton.setOnClickListener { changeReadStatusOrAskForPermission() }
        newNoteButton.setOnClickListener {
            DbHelper(this).saveNotes(book.id, editText.text.toString())
            editText.setText("")
            Toast.makeText(this, R.string.added, Toast.LENGTH_SHORT).show()
        }
        showDesc.setOnClickListener {
            val intent = Intent(this, DescriptionShow::class.java)
            intent.putExtra(BookDetailsActivity.BOOK_INTENT, book)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
        showNotes.setOnClickListener {
            val intent = Intent(this, NotesShow::class.java)
            intent.putExtra(BookDetailsActivity.BOOK_INTENT, book)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
        if(book.title != null) {
            titleText.setText(book.title)
        }
        authorsText.setText(book.getAuthorsText())
        if(book.pageCount != null) {
            pagenumText.setText(book.pageCount.toString())
        }
        if(book.averageRating != null) {
            ratingText.setText(book.averageRating.toString() + "(" + book.ratingCount.toString() + ")")
        }
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
    private fun enableTransition() {
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()
            exitTransition.duration = 1000
        }
    }
}
