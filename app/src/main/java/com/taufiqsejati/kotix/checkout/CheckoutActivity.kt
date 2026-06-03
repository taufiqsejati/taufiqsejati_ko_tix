package com.taufiqsejati.kotix.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taufiqsejati.kotix.checkout.adapter.CheckoutAdapter
import com.taufiqsejati.kotix.checkout.model.Checkout
import com.taufiqsejati.kotix.databinding.ActivityCheckoutBinding
import com.taufiqsejati.kotix.utils.Preferences
import com.taufiqsejati.kotix.utils.getSerializableData

class CheckoutActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityCheckoutBinding
    private var dataList = ArrayList<Checkout>()
    private var total: Int = 0
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Setup view binding
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferences = Preferences(this)
        val intentData = intent.getSerializableData<ArrayList<Checkout>>("data")

        if (intentData != null) {
            dataList = intentData

            // Hitung total harga dari data kursi yang sukses diambil
            for (item in dataList) {
                total += item.harga?.toIntOrNull() ?: 0
            }

            // Tambahkan baris total ke list untuk RecyclerView
            dataList.add(Checkout("Total harus dibayar", total.toString()))

            // Inisialisasi RecyclerView kamu
            binding.rcCheckout.layoutManager = LinearLayoutManager(this)
            binding.rcCheckout.adapter = CheckoutAdapter(dataList) {}

            binding.btnTiket.setOnClickListener {
                var intent = Intent(this, CheckoutSuccessActivity::class.java)
                startActivity(intent)
            }
            binding.ivBack.setOnClickListener {
                finish()
            }
        } else {
            // Antisipasi jika data kosong agar tidak BLANK tanpa informasi
            Toast.makeText(this, "Gagal memuat data kursi yang dipilih", Toast.LENGTH_LONG).show()
        }
    }
}
