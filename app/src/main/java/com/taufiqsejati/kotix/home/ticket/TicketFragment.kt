package com.taufiqsejati.kotix.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taufiqsejati.kotix.databinding.FragmentTicketBinding
import com.taufiqsejati.kotix.home.dashboard.ComingSoonAdapter
import com.taufiqsejati.kotix.home.model.Film
import com.taufiqsejati.kotix.home.ticket.TicketActivity
import com.taufiqsejati.kotix.utils.Preferences
import kotlin.jvm.java

class TicketFragment : Fragment() {

    // Inisialisasi View Binding untuk Fragment
    private var _binding: FragmentTicketBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var preferences: Preferences
    private lateinit var mDataBase: DatabaseReference
    private var dataList = ArrayList<Film>()

    // 1. onCreateView: Tempat khusus untuk MEMASANG LAYOUT XML
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 2. onViewCreated: GANTINYA onActivityCreated (Tempat Logika Koding Kamu)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DI SINI tempat kamu menaruh penanganan klik tombol, adapter, firebase, dll.
        // Contoh memanggil komponen via View Binding:
        /*
        binding.btnLogOut.setOnClickListener {
            Toast.makeText(context, "Log Out Berhasil", Toast.LENGTH_SHORT).show()
        }
        */

        preferences = Preferences(requireContext())
        mDataBase = FirebaseDatabase.getInstance().getReference("Film")

        binding.rcTiket.layoutManager = LinearLayoutManager(context)
        getData()
    }

    private fun getData() {
        mDataBase.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    if (context != null) {
                        Toast.makeText(context, databaseError.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataList.clear()
                    for (getdataSnapshot in dataSnapshot.children) {
                        val film = getdataSnapshot.getValue(Film::class.java)
                        film?.let { dataList.add(it) } // Null safety saat add ke list
                    }

                    // Cek isi context agar tidak crash saat fragment dihancurkan saat proses
                    // asinkron masih jalan
                    if (context != null) {
                        binding.rcTiket.adapter =
                            ComingSoonAdapter(dataList) {
                                // Action onClick jika dibutuhkan
                                var intent =
                                    Intent(
                                            context,
                                            TicketActivity::class.java,
                                        )
                                        .putExtra("data", it)
                                startActivity(intent)
                            }
                        binding.tvTotal.setText(dataList.size.toString() + " Movies")
                    }
                }
            }
        )
    }

    // 3. onDestroyView: Wajib membersihkan binding agar tidak terjadi kebocoran memori (memory
    // leak)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
