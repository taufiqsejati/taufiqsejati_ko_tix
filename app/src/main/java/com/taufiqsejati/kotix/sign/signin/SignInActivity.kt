package com.taufiqsejati.kotix.sign.signin

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
import com.taufiqsejati.kotix.databinding.ActivitySignInBinding
import com.taufiqsejati.kotix.home.HomeActivity
import com.taufiqsejati.kotix.sign.signup.SignUpActivity
import com.taufiqsejati.kotix.utils.Preferences

class SignInActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivitySignInBinding
    private lateinit var iUsername: String
    private lateinit var iPassword: String
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preference: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Setup view binding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDatabase = FirebaseDatabase.getInstance().getReference("User")
        preference = Preferences(this)

        preference.setValues("onboarding", "1")
        if (preference.getValues("status").equals("1")) {
            finishAffinity()

            var intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Panggil btn_home lewat objek binding
        binding.btnHome.setOnClickListener {
            iUsername = binding.etUsername.text.toString()
            iPassword = binding.etPassword.text.toString()

            if (iUsername.equals("")) {
                binding.etUsername.error = "Silahkan tulis username Anda"
                binding.etUsername.requestFocus()
            } else if (iPassword.equals("")) {
                binding.etPassword.error = "Silahkan tulis Password Anda"
                binding.etPassword.requestFocus()
            } else {
                pushLogin(iUsername, iPassword)
            }
        }

        binding.btnDaftar.setOnClickListener {
            var intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase
            .child(iUsername)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            Toast.makeText(
                                    this@SignInActivity,
                                    "Username tidak ditemukan",
                                    Toast.LENGTH_LONG,
                                )
                                .show()
                        } else {

                            if (user.password.equals(iPassword)) {
                                preference.setValues("nama", user.nama.toString())
                                preference.setValues("username", user.username.toString())
                                preference.setValues("url", user.url.toString())
                                preference.setValues("email", user.email.toString())
                                preference.setValues("saldo", user.saldo.toString())
                                preference.setValues("status", "1")
                                var intent = Intent(this@SignInActivity, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                        this@SignInActivity,
                                        "Password Anda Salah",
                                        Toast.LENGTH_LONG,
                                    )
                                    .show()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                                this@SignInActivity,
                                databaseError.message,
                                Toast.LENGTH_LONG,
                            )
                            .show()
                    }
                }
            )
    }
}
