package com.example.mybooksthoughts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.books.BooksScopes
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(BooksScopes.BOOKS))
            .requestServerAuthCode(Keys.CLIENT_ID)
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        signOutButton.visibility = View.GONE
        signInButton.setOnClickListener { signIn() }
        signOutButton.setOnClickListener { signOut() }

        readBooksButton.setOnClickListener {
            val intent = Intent(this, ReadBooksActivity::class.java)
            startActivity(intent)
        }

        searchBooksButton.setOnClickListener {
            val intent = Intent(this, SearchBooksActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(googleAccount)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                signInButton.visibility = View.VISIBLE
                signOutButton.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(signInTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = signInTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w("LOGIN_FAIL", "signInResult:failed code=" + e.statusCode);
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if(account != null) {
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
            titleTextView.text = "Hello " + account.email
        }
    }
}
