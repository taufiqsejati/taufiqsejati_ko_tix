package com.taufiqsejati.kotix.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taufiqsejati.kotix.databinding.ActivityOnboardingOneBinding // Import binding otomatis
import com.taufiqsejati.kotix.sign.signin.SignInActivity
import com.taufiqsejati.kotix.utils.Preferences

class OnboardingOneActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityOnboardingOneBinding
    private lateinit var preference: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Setup view binding
        binding = ActivityOnboardingOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = Preferences(this)

        if (preference.getValues("onboarding").equals("1")) {
            finishAffinity()
            // Aksi ketika tombol diklik, misalnya pindah ke halaman daftar
            var intent = Intent(this@OnboardingOneActivity, SignInActivity::class.java)
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
            // Aksi ketika tombol diklik, misalnya pindah ke halaman home
            var intent = Intent(this@OnboardingOneActivity, OnboardingTwoActivity::class.java)
            startActivity(intent)
        }

        // 5. Panggil btn_daftar lewat objek binding
        binding.btnDaftar.setOnClickListener {
            finishAffinity()
            // Aksi ketika tombol diklik, misalnya pindah ke halaman daftar
            var intent = Intent(this@OnboardingOneActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
