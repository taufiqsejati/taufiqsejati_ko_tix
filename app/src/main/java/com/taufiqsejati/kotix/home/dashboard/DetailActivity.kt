package com.taufiqsejati.kotix.home.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taufiqsejati.kotix.checkout.PilihBangkuActivity
import com.taufiqsejati.kotix.databinding.ActivityDetailBinding
import com.taufiqsejati.kotix.home.model.Film
import com.taufiqsejati.kotix.home.model.Plays
import com.taufiqsejati.kotix.utils.getSerializableData
import kotlin.jvm.java

class DetailActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mDatabase: DatabaseReference
    private var dataList = ArrayList<Plays>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Setup view binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tinggal panggil langsung di sini!
        val data = intent.getSerializableData<Film>("data")
        mDatabase =
            FirebaseDatabase.getInstance()
                .getReference("Film")
                .child(data?.judul.toString())
                .child("play")

        binding.tvKursi.text = data?.judul
        binding.tvGenre.text = data?.genre
        binding.tvDesc.text = data?.desc
        binding.tvRate.text = data?.rating

        Glide.with(this).load(data?.poster).into(binding.ivPoster)
        binding.btnPilihBangku.setOnClickListener {
            val intent =
                Intent(
                        this@DetailActivity,
                        PilihBangkuActivity::class.java,
                    )
                    .putExtra("data", data)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.rvWhoPlay.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        getData()
    }

    private fun getData() {
        mDatabase.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@DetailActivity, "" + p0.message, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    dataList.clear()

                    for (getdataSnapshot in p0.children) {
                        var Film = getdataSnapshot.getValue(Plays::class.java)
                        dataList.add(Film!!)
                    }

                    binding.rvWhoPlay.adapter = PlaysAdapter(dataList) {}
                }
            }
        )
    }
}
