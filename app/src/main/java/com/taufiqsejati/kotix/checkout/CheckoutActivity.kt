package com.taufiqsejati.kotix.checkout

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.taufiqsejati.kotix.R
import com.taufiqsejati.kotix.checkout.adapter.CheckoutAdapter
import com.taufiqsejati.kotix.checkout.model.Checkout
import com.taufiqsejati.kotix.databinding.ActivityCheckoutBinding
import com.taufiqsejati.kotix.home.model.Film
import com.taufiqsejati.kotix.home.ticket.TicketActivity
import com.taufiqsejati.kotix.utils.Preferences
import com.taufiqsejati.kotix.utils.getSerializableData
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    // 1. Inisialisasi variabel binding
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var databaseReference: DatabaseReference
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
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        val intentData = intent.getSerializableData<ArrayList<Checkout>>("data")
        val data = intent.getSerializableData<Film>("datas")

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

            //            binding.btnTiket.setOnClickListener {
            //                var intent = Intent(this, CheckoutSuccessActivity::class.java)
            //                startActivity(intent)
            //
            //                showNotif(data)
            //            }

            binding.btnTiket.setOnClickListener {
                // Ambil ID / Username dari preferences. Jika null, fallback ke "1" sesuai gambar
                // database
                val username = preferences.getValues("username") ?: "1"

                if (username.isNotEmpty()) {
                    val currentSaldoStr = preferences.getValues("saldo") ?: "0"
                    val currentSaldo = currentSaldoStr.toIntOrNull() ?: 0

                    // Hitung sisa saldo setelah dikurangi total belanjaan
                    val sisaSaldo = currentSaldo - total

                    // Validasi tambahan agar saldo tidak minus secara tidak sengaja di client side
                    if (sisaSaldo >= 0) {

                        // Ubah sisaSaldo menjadi String (.toString()) agar sesuai dengan format di
                        // Firebase kamu
                        databaseReference
                            .child(username)
                            .child("saldo")
                            .setValue(sisaSaldo.toString())
                            .addOnSuccessListener {

                                // PENTING: Update saldo di Preferences lokal kamu agar sinkron!
                                preferences.setValues("saldo", sisaSaldo.toString())

                                // Pindah ke activity berikutnya
                                val intent =
                                    Intent(
                                        this@CheckoutActivity,
                                        CheckoutSuccessActivity::class.java,
                                    )
                                startActivity(intent)

                                showNotif(data)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                        this@CheckoutActivity,
                                        "Transaksi gagal! Terjadi kesalahan network: ${e.message}",
                                        Toast.LENGTH_LONG,
                                    )
                                    .show()
                            }
                    } else {
                        Toast.makeText(
                                this@CheckoutActivity,
                                "Saldo pada e-wallet kamu tidak mencukupi!",
                                Toast.LENGTH_LONG,
                            )
                            .show()
                    }
                } else {
                    Toast.makeText(
                            this@CheckoutActivity,
                            "Username tidak ditemukan!",
                            Toast.LENGTH_LONG,
                        )
                        .show()
                }
            }

            if (preferences.getValues("saldo")!!.isNotEmpty()) {
                val localeID = Locale.forLanguageTag("id-ID")
                val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                binding.tvSaldo.setText(
                    formatRupiah.format(preferences.getValues("saldo")!!.toDouble())
                )
                binding.btnTiket.visibility = View.VISIBLE
                binding.textView3.visibility = View.INVISIBLE
            } else {
                binding.tvSaldo.setText("Rp 0")
                binding.btnTiket.visibility = View.INVISIBLE
                binding.textView3.visibility = View.VISIBLE
                binding.textView3.text =
                    "Saldo pada e-wallet kamu tidak mencukupi\n" + "untuk melakukan transaksi"
            }

            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.btnHome.setOnClickListener {
                finish()
            }
        } else {
            // Antisipasi jika data kosong agar tidak BLANK tanpa informasi
            Toast.makeText(this, "Gagal memuat data kursi yang dipilih", Toast.LENGTH_LONG).show()
        }
    }

    private fun showNotif(datas: Film?) {
        val NOTIFICATION_CHANNEL_ID = "channel_bwa_notif"
        val context = this.applicationContext
        var notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = "KoTix Notif Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        //        val mIntent = Intent(this, CheckoutSuccessActivity::class.java)
        //        val bundle = Bundle()
        //        bundle.putString("id", "id_film")
        //        mIntent.putExtras(bundle)

        val mIntent = Intent(this, TicketActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("data", datas)
        mIntent.putExtras(bundle)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        builder
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_mov)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.logo_notification,
                )
            )
            .setTicker("notif bwa starting")
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentTitle("Sukses Terbeli")
            .setContentText(
                "Tiket ${datas?.judul ?: "Film"} berhasil kamu dapatkan. Enjoy the movie!"
            )

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(115, builder.build())
    }
}
