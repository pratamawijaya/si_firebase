package id.pratama.week10firebase.model

import java.util.Date


data class Note(
    val id: String? = "",
    val judul: String,
    val deskripsi: String,
    val createdAt: Date
)
