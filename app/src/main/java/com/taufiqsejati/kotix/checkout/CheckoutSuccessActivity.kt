package com.taufiqsejati.kotix.checkout

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taufiqsejati.kotix.databinding.ActivityCheckoutSuccessBinding
import com.taufiqsejati.kotix.home.HomeActivity
import kotlin.jvm.java

class CheckoutSuccessActivity : AppCompatActivity() {
    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityCheckoutSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Setup view binding
        binding = ActivityCheckoutSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnHome.setOnClickListener {
            finishAffinity()

            val intent =
                Intent(
                    this@CheckoutSuccessActivity,
                    HomeActivity::class.java,
                )
            startActivity(intent)
        }
    }
}
