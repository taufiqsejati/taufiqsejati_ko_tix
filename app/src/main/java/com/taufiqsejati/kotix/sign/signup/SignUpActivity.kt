package com.taufiqsejati.kotix.sign.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taufiqsejati.kotix.databinding.ActivitySignUpBinding
import com.taufiqsejati.kotix.sign.signin.User

class SignUpActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sUsername: String
    private lateinit var sPassword: String
    private lateinit var sNama: String
    private lateinit var sEmail: String
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Setup view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabaseReference = mFirebaseInstance.getReference("User")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLanjutkan.setOnClickListener {
            sUsername = binding.etUsername.text.toString()
            sPassword = binding.etPassword.text.toString()
            sNama = binding.etNama.text.toString()
            sEmail = binding.etEmail.text.toString()

            if (sUsername.equals("")) {
                binding.etUsername.error = "Silahkan isi username Anda"
                binding.etUsername.requestFocus()
            } else if (sPassword.equals("")) {
                binding.etPassword.error = "Silahkan isi password Anda"
                binding.etPassword.requestFocus()
            } else if (sNama.equals("")) {
                binding.etNama.error = "Silahkan isi nama Anda"
                binding.etNama.requestFocus()
            } else if (sEmail.equals("")) {
                binding.etEmail.error = "Silahkan isi email Anda"
                binding.etEmail.requestFocus()
            } else {
                saveUsername(sUsername, sPassword, sNama, sEmail)
            }
        }
    }

    private fun saveUsername(sUsername: String, sPassword: String, sNama: String, sEmail: String) {
        var user = User()
        user.username = sUsername
        user.password = sPassword
        user.nama = sNama
        user.email = sEmail

        if (sUsername != null) {
            checkingUsername(sUsername, user)
        }
    }

    private fun checkingUsername(sUsername: String, data: User) {
        mDatabaseReference
            .child(sUsername)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            mDatabaseReference.child(sUsername).setValue(data)

                            var intent =
                                Intent(this@SignUpActivity, SignUpPhotoscreenActivity::class.java)
                                    .putExtra("nama", data.nama)
                                    .putExtra("username", data.username)
                            startActivity(intent)
                        } else {

                            Toast.makeText(
                                    this@SignUpActivity,
                                    "User sudah digunakan",
                                    Toast.LENGTH_LONG,
                                )
                                .show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                                this@SignUpActivity,
                                "" + databaseError.message,
                                Toast.LENGTH_LONG,
                            )
                            .show()
                    }
                }
            )
    }
}
