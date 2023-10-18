package id.pratama.week10firebase.model

import com.google.firebase.firestore.FieldValue
import java.util.Date


data class Note(
    val judul: String,
    val deskripsi: String,
    val createdAt: Date
)
