package com.taufiqsejati.kotix.home

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.taufiqsejati.kotix.R
import com.taufiqsejati.kotix.databinding.ActivityHomeBinding
import com.taufiqsejati.kotix.home.dashboard.DashboardFragment
import com.taufiqsejati.kotix.home.setting.SettingFragment

class HomeActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Setup view binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Sesuaikan id 'main' menggunakan binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentHome = DashboardFragment()
        val fragmentTicket = TicketFragment()
        val fragmentSetting = SettingFragment()

        setFragment(fragmentHome)

        binding.ivMenu1.setOnClickListener {
            setFragment(fragmentHome)

            changeIcon(binding.ivMenu1, R.drawable.ic_home_active)
            changeIcon(binding.ivMenu2, R.drawable.ic_tiket)
            changeIcon(binding.ivMenu3, R.drawable.ic_profile)
        }

        binding.ivMenu2.setOnClickListener {
            setFragment(fragmentTicket)

            changeIcon(binding.ivMenu1, R.drawable.ic_home)
            changeIcon(binding.ivMenu2, R.drawable.ic_tiket_active)
            changeIcon(binding.ivMenu3, R.drawable.ic_profile)
        }

        binding.ivMenu3.setOnClickListener {
            setFragment(fragmentSetting)

            changeIcon(binding.ivMenu1, R.drawable.ic_home)
            changeIcon(binding.ivMenu2, R.drawable.ic_tiket)
            changeIcon(binding.ivMenu3, R.drawable.ic_profile_active)
        }
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layout_frame, fragment)
        fragmentTransaction.commit()
    }

    private fun changeIcon(imageView: ImageView, int: Int) {
        imageView.setImageResource(int)
    }
}
