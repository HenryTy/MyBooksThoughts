package com.example.mybooksthoughts

import com.google.api.services.books.model.Volume
import java.io.Serializable

class Book(val id: String,
           val title: String,
           val authors: List<String>,
           val description: String?,
           val pageCount: Int?,
           val averageRating: Double?,
           val ratingCount: Int?,
           val imageUrl: String?): Serializable {

    companion object {
        fun createFromVolume(volume: Volume): Book {
            val id = volume.id
            val title =  volume.volumeInfo.title
            val authors = volume.volumeInfo.authors
            val description = volume.volumeInfo.description
            val pageCount = volume.volumeInfo.pageCount
            val averageRating = volume.volumeInfo.averageRating
            val ratingCount = volume.volumeInfo.ratingsCount
            val imageUrl = volume.volumeInfo.imageLinks.thumbnail
            return Book(id, title, authors, description, pageCount,
                averageRating, ratingCount, imageUrl)
        }
    }

    fun getAuthorsText(): String {
        var text = ""
        for(author in authors) {
            if(text != "") text += ", "
            text += author
        }
        return text
    }
}