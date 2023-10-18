package id.pratama.week10firebase.presentation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupieAdapter
import id.pratama.week10firebase.R
import id.pratama.week10firebase.model.Note
import id.pratama.week10firebase.presentation.add_note.AddNoteActivity
import id.pratama.week10firebase.presentation.home.rvitem.NoteRvItem
import id.pratama.week10firebase.presentation.home.rvitem.NoteRvItemListener
import id.pratama.week10firebase.presentation.login.LoginActivity

class HomeActivity : AppCompatActivity(), NoteRvItemListener {


    private val db = Firebase.firestore

    companion object {
        const val PATH_NOTES = "notes"
        const val PATH_USER_NOTES = "user_notes"
    }

    private val listNotes = mutableListOf<Note>()

    private val adapter = GroupieAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnAddNote = findViewById<FloatingActionButton>(R.id.btnAddNote)
        val rvNotes = findViewById<RecyclerView>(R.id.recyclerView)
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        btnAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val currentUser = Firebase.auth.currentUser

        val dataRef = db.collection(PATH_NOTES).document(currentUser?.uid ?: "")
            .collection(PATH_USER_NOTES)
            .orderBy("createdAt")


        // kita listen perubahan data yang ada di firestore
        dataRef.addSnapshotListener { value, error ->
            val documents = value?.documents


            documents?.let {
                // jika sudah ada data sebelumnya, clear data terlebih dahulu
                listNotes.clear()
                adapter.clear()
                //
                it.map { qDoc ->
                    Log.d("tag", "hasil -> ${qDoc.data.toString()}")
                    val docTimeStamp = qDoc.data!!["createdAt"] as Timestamp
                    listNotes.add(
                        Note(
                            id = qDoc.id,
                            judul = qDoc.data!!["judul"].toString(),
                            deskripsi = qDoc.data!!["deskripsi"].toString(),
                            createdAt = docTimeStamp.toDate()
                        )
                    )
                }

                listNotes.map { note ->
                    Log.d("tag", "data -> $note")
                    adapter.add(NoteRvItem(note, this))
                }
            }


        }


    }

    override fun onItemDeleted(docId: String?) {
        docId?.let {
            Log.d("tag", "delet data with id -> $docId")
            val currentUser = Firebase.auth.currentUser
            db.collection(PATH_NOTES)
                .document(currentUser?.uid ?: "")
                .collection(PATH_USER_NOTES)
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d("tag", "success delete data with id $docId")
                }
                .addOnFailureListener {
                    Log.e("tag", "gagal delete data ${it.localizedMessage}")
                }
        }
    }
}