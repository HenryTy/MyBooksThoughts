package com.example.mybooksthoughts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.description_show.*


class DescriptionShow: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.description_show)
        descText.setText(BookDetailsActivity().book.description)
    }
}