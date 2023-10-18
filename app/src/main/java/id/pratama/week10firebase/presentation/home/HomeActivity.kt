package id.pratama.week10firebase.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import id.pratama.week10firebase.R
import id.pratama.week10firebase.model.Note
import id.pratama.week10firebase.presentation.add_note.AddNoteActivity
import id.pratama.week10firebase.presentation.login.LoginActivity
import java.util.Date

class HomeActivity : AppCompatActivity() {


    private val db = Firebase.firestore

    companion object {
        const val PATH_NOTES = "notes"
        const val PATH_USER_NOTES = "user_notes"
    }

    private val listNotes = mutableListOf<Note>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnAddNote = findViewById<FloatingActionButton>(R.id.btnAddNote)

        btnAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val currentUser = Firebase.auth.currentUser
        db.collection(PATH_NOTES).document(currentUser?.uid ?: "")
            .collection(PATH_USER_NOTES)
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener {
                it.map { qDoc ->
                    Log.d("tag", "hasil -> ${qDoc.data.toString()}")
                    val docTimeStamp = qDoc.data["createdAt"] as com.google.firebase.Timestamp
                    listNotes.add(
                        Note(
                            judul = qDoc.data["judul"].toString(),
                            deskripsi = qDoc.data["deskripsi"].toString(),
                            createdAt = docTimeStamp.toDate()
                        )
                    )
                }

                Log.d("tag", "selesai mapping listNotes hasil -> ${listNotes.size}")
                listNotes.map {
                    Log.d("tag", "data -> $it")
                }

            }
            .addOnFailureListener {
                Log.e("tag", "error -> ${it.localizedMessage}")
            }
    }
}