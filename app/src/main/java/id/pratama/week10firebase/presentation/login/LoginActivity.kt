package id.pratama.week10firebase.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.pratama.week10firebase.R
import id.pratama.week10firebase.presentation.home.HomeActivity
import java.util.Calendar

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        val RC_GOOGLE_SIGNIN = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // inisiasi button
        val btnSignIn = findViewById<SignInButton>(R.id.btnSignWithGoogle)


        // inisiasi buat kebutuhan tombol sigin with google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // inisiasi firebase auth
        auth = Firebase.auth

        // check apakah user sudah login sebelumnya
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // user sudah login,
            // navigate to home
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        btnSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGNIN)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGNIN) {

            // ambil data google account yang dipake user
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("pratama_tag", "firebaseAuthWithGoogle: ${account.idToken}")

                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("pratama_tag", "error -> ${e.localizedMessage}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("pratama_tag", "token -> $idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("pratama_tag", "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        this,
                        "Berhasil sign in ${user?.displayName}",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("pratama_tag", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Gagal sign in", Toast.LENGTH_SHORT).show()
                }
            }
    }
}