package com.taufiqsejati.kotix.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.taufiqsejati.kotix.databinding.FragmentTicketBinding

class TicketFragment : Fragment() {

    // Inisialisasi View Binding untuk Fragment
    private var _binding: FragmentTicketBinding? = null
    private val binding
        get() = _binding!!

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
    }

    // 3. onDestroyView: Wajib membersihkan binding agar tidak terjadi kebocoran memori (memory
    // leak)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
