package com.taufiqsejati.kotix.home.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.taufiqsejati.kotix.databinding.FragmentSettingBinding
import com.taufiqsejati.kotix.utils.Preferences

class SettingFragment : Fragment() {

    // Inisialisasi View Binding untuk Fragment
    private var _binding: FragmentSettingBinding? = null
    private val binding
        get() = _binding!!

    lateinit var preferences: Preferences

    // 1. onCreateView: Tempat khusus untuk MEMASANG LAYOUT XML
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
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

        binding.ivNama.text = preferences.getValues("nama")
        binding.tvEmail.text = preferences.getValues("email")

        Glide.with(this)
            .load(preferences.getValues("url"))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ivProfile)

        binding.tvMyWallet.setOnClickListener {
            //            startActivity(Intent(activity, MyWalletActivity::class.java))
        }
    }

    // 3. onDestroyView: Wajib membersihkan binding agar tidak terjadi kebocoran memori (memory
    // leak)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
