package com.taufiqsejati.kotix.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taufiqsejati.kotix.databinding.ActivityOnboardingTwoBinding // Import binding otomatis

class OnboardingTwoActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityOnboardingTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Setup view binding
        binding = ActivityOnboardingTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Panggil btn_home lewat objek binding
        binding.btnHome.setOnClickListener {
            // Aksi ketika tombol diklik, misalnya pindah ke halaman home
            var intent = Intent(this@OnboardingTwoActivity, OnboardingThreeActivity::class.java)
            startActivity(intent)
        }

    }
}