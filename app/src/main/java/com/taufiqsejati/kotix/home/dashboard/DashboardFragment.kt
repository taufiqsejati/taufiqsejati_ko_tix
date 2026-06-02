package com.taufiqsejati.kotix.home.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taufiqsejati.kotix.databinding.FragmentDashboardBinding
import com.taufiqsejati.kotix.home.model.Film
import com.taufiqsejati.kotix.utils.Preferences
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var preferences: Preferences
    private lateinit var mDataBase: DatabaseReference

    private var dataList = ArrayList<Film>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SOLUSI 'activity!!': Gunakan requireContext() yang jauh lebih aman untuk inisialisasi
        // lokal
        preferences = Preferences(requireContext())
        mDataBase = FirebaseDatabase.getInstance().getReference("Film")

        // Set nama user
        binding.tvNama.text = preferences.getValues("nama")

        // PERBAIKAN LOGIKA: Periksa apakah saldo TIDAK kosong dan TIDAK null sebelum konversi
        val saldoRaw = preferences.getValues("saldo")
        if (!saldoRaw.isNullOrEmpty()) {
            try {
                currency(saldoRaw.toDouble(), binding.tvSaldo)
            } catch (e: NumberFormatException) {
                binding.tvSaldo.text = "Rp0"
            }
        } else {
            binding.tvSaldo.text = "Rp0" // Fallback jika kosong
        }

        // Load foto profil
        Glide.with(this)
            .load(preferences.getValues("url"))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ivProfile)

        // Setup LayoutManager untuk RecyclerView
        binding.rvNowPlaying.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvComingSoon.layoutManager = LinearLayoutManager(context)

        getData()
    }

    private fun getData() {
        mDataBase.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataList.clear()
                    for (getdataSnapshot in dataSnapshot.children) {
                        val film = getdataSnapshot.getValue(Film::class.java)
                        film?.let { dataList.add(it) } // Null safety saat add ke list
                    }

                    // Cek isi context agar tidak crash saat fragment dihancurkan saat proses
                    // asinkron masih jalan
                    if (context != null) {
                        binding.rvNowPlaying.adapter =
                            NowPlayingAdapter(dataList) {
                                // Action onClick jika dibutuhkan
                                var intent =
                                    Intent(
                                            context,
                                            DetailActivity::class.java,
                                        )
                                        .putExtra("data", it)
                                startActivity(intent)
                            }

                        binding.rvComingSoon.adapter =
                            ComingSoonAdapter(dataList) {
                                // Action onClick jika dibutuhkan
                                var intent =
                                    Intent(
                                            context,
                                            DetailActivity::class.java,
                                        )
                                        .putExtra("data", it)
                                startActivity(intent)
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    if (context != null) {
                        Toast.makeText(context, databaseError.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    private fun currency(harga: Double, textView: TextView) {
        val localID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localID)
        textView.text = format.format(harga)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
