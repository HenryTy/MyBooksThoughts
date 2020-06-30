package com.example.mybooksthoughts

import com.google.api.services.books.model.Volume

class Book(val title: String) {
    companion object {
        fun createFromVolume(volume: Volume): Book {
            val title =  volume.volumeInfo.title
            return Book(title)
        }
    }
}