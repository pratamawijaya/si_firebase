package id.pratama.week10firebase.presentation.add_note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.pratama.week10firebase.R
import id.pratama.week10firebase.model.Note
import java.util.Date

class AddNoteActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    companion object {
        const val PATH_NOTES = "notes"
        const val PATH_USER_NOTES = "user_notes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val inputJudul = findViewById<EditText>(R.id.inputJudul)
        val inputDesc = findViewById<EditText>(R.id.inputDesc)

        btnSimpan.setOnClickListener {
            val judul = inputJudul.text.toString()
            val desc = inputDesc.text.toString()
            val currentUser = Firebase.auth.currentUser

            val note = Note(
                judul = judul,
                deskripsi = desc,
                createdAt = Date()
            )

            val dataUser = mapOf(
                "name" to "${currentUser?.displayName}",
                "email" to "${currentUser?.email}"
            )

            db.collection(PATH_NOTES)
                .document(currentUser?.uid ?: "")
                .set(dataUser)

            db.collection(PATH_NOTES)
                .document(currentUser?.uid ?: "")
                .collection(PATH_USER_NOTES)
                .add(note)
                .addOnSuccessListener {
                    Log.d("tag", "success simpan ${it.toString()}")
                    // success
                    finish()
                }
                .addOnFailureListener {
                    Log.e("tag", "gagal simpan ${it.localizedMessage}")
                }

        }
    }
}