package com.taufiqsejati.kotix.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taufiqsejati.kotix.R
import com.taufiqsejati.kotix.checkout.model.Checkout
import com.taufiqsejati.kotix.databinding.ActivityPilihBangkuBinding
import com.taufiqsejati.kotix.home.model.Film
import com.taufiqsejati.kotix.utils.getSerializableData
import kotlin.jvm.java

class PilihBangkuActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityPilihBangkuBinding
    var statusA3: Boolean = false
    var statusA4: Boolean = false
    var total: Int = 0

    private var dataList = ArrayList<Checkout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Setup view binding
        binding = ActivityPilihBangkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val data = intent.getSerializableData<Film>("data")
        binding.tvKursi.text = data?.judul

        binding.a3.setOnClickListener {
            if (statusA3) {
                binding.a3.setImageResource(R.drawable.ic_rectangle_empty)
                statusA3 = false
                total -= 1
                beliTiket(total)
            } else {
                binding.a3.setImageResource(R.drawable.ic_rectangle_selected)
                statusA3 = true
                total += 1
                beliTiket(total)

                val data = Checkout("A3", "70000")
                dataList.add(data)
            }
        }
        binding.a4.setOnClickListener {
            if (statusA4) {
                binding.a4.setImageResource(R.drawable.ic_rectangle_empty)
                statusA4 = false
                total -= 1
                beliTiket(total)
            } else {
                binding.a4.setImageResource(R.drawable.ic_rectangle_selected)
                statusA4 = true
                total += 1
                beliTiket(total)

                val data = Checkout("A4", "70000")
                dataList.add(data)
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnHome.setOnClickListener {
            // 1. Kosongkan list terlebih dahulu untuk mencegah duplikasi dari sisa data sebelumnya
            dataList.clear()

            // 2. Scan ulang kursi mana saja yang saat ini statusnya sedang dipilih (true)
            if (statusA3) {
                dataList.add(Checkout("A3", "70000"))
            }
            if (statusA4) {
                dataList.add(Checkout("A4", "70000"))
            }

            // Tambahkan kondisi 'if' serupa jika nanti kamu menambah kursi lain (misal: A1, A2,
            // dst)

            // 3. Kirim data yang sudah bersih ke CheckoutActivity
            val intent = Intent(this, CheckoutActivity::class.java).putExtra("data", dataList)
            startActivity(intent)
        }
    }

    private fun beliTiket(total: Int) {
        if (total == 0) {
            binding.btnHome.setText("Beli Tiket")
            binding.btnHome.visibility = View.INVISIBLE
        } else {
            binding.btnHome.setText("Beli Tiket (" + total + ")")
            binding.btnHome.visibility = View.VISIBLE
        }
    }
}
