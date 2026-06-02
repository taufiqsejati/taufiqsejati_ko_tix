package com.taufiqsejati.kotix.sign.signup

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.taufiqsejati.kotix.R
import com.taufiqsejati.kotix.databinding.ActivitySignUpPhotoscreenBinding
import com.taufiqsejati.kotix.home.HomeActivity
import com.taufiqsejati.kotix.utils.Preferences
import java.io.File
import java.util.UUID

class SignUpPhotoscreenActivity : AppCompatActivity(), PermissionListener {

    private lateinit var binding: ActivitySignUpPhotoscreenBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var statusAdd: Boolean = false
    private var filePath: Uri? = null // Nullable karena awalnya kosong

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    // Tambahkan inisialisasi DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var preferences: Preferences
    private var username: String? = ""

    // --- CARA BARU: BERSIH & AMAN DARI DEPRECATED ---
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && filePath != null) {
                statusAdd = true

                // Tampilkan foto langsung menggunakan URI lokal temporer
                Glide.with(this)
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.ivProfile)

                binding.btnSave.visibility = View.VISIBLE
                binding.ivAdd.setImageResource(R.drawable.ic_btn_delete)
            } else {
                Toast.makeText(this, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpPhotoscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Inisialisasi Database ke node "User"
        databaseReference = FirebaseDatabase.getInstance().getReference("User")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- DAFTARKAN ON BACK PRESSED CALLBACK DI SINI ---
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Tulis logika kamu di sini saat user menekan back
                    Toast.makeText(
                            this@SignUpPhotoscreenActivity,
                            "Tergesah? Klik tombol upload nanti aja",
                            Toast.LENGTH_LONG,
                        )
                        .show()

                    // Jika kamu ingin menutup activity secara normal setelah Toast muncul,
                    // hapus tanda komentar pada baris di bawah ini:
                    // finish()
                }
            },
        )

        // Ambil nama dan username yang dikirim dari halaman pendaftaran sebelumnya
        val namaUser = intent.getStringExtra("nama")
        username = intent.getStringExtra("username")

        binding.tvHello.text = "Selamat Datang\n" + namaUser

        binding.ivAdd.setOnClickListener {
            if (statusAdd) {
                statusAdd = false
                binding.btnSave.visibility = View.VISIBLE
                binding.ivAdd.setImageResource(R.drawable.ic_btn_upload)
                binding.ivProfile.setImageResource(R.drawable.user_pic)
                filePath = null
            } else {
                Dexter.withActivity(this)
                    .withPermission(android.Manifest.permission.CAMERA)
                    .withListener(this)
                    .check()
            }
        }

        binding.btnHome.setOnClickListener {
            finishAffinity()
            val intent = Intent(this@SignUpPhotoscreenActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnSave.setOnClickListener {
            if (filePath != null) {
                val progressDialog =
                    ProgressDialog(this).apply {
                        setTitle("Uploading...")
                        setCancelable(false) // Mencegah user membatalkan di tengah jalan
                        show()
                    }

                val ref = storageReference.child("images/" + UUID.randomUUID().toString())
                ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Uploaded", Toast.LENGTH_LONG).show()

                        ref.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            preferences.setValues("url", uri.toString())

                            if (!username.isNullOrEmpty()) {
                                databaseReference
                                    .child(username!!)
                                    .child("url")
                                    .setValue(imageUrl)
                                    .addOnSuccessListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                                this@SignUpPhotoscreenActivity,
                                                "Profile Updated Successful",
                                                Toast.LENGTH_LONG,
                                            )
                                            .show()

                                        // Pindah Halaman setelah database sukses terupdate
                                        finishAffinity()
                                        val intent =
                                            Intent(
                                                this@SignUpPhotoscreenActivity,
                                                HomeActivity::class.java,
                                            )
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                                this@SignUpPhotoscreenActivity,
                                                "Database Error: ${e.message}",
                                                Toast.LENGTH_LONG,
                                            )
                                            .show()
                                    }
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(
                                        this@SignUpPhotoscreenActivity,
                                        "Username tidak ditemukan!",
                                        Toast.LENGTH_LONG,
                                    )
                                    .show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed to upload", Toast.LENGTH_LONG).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Upload ${progress.toInt()} %")
                    }
            } else {
                Toast.makeText(this, "Silakan ambil foto terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // --- DEXTER PERMISSION OVERRIDES ---

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        // Panggil fungsi untuk menyiapkan URI file kosong sebelum kamera dibuka
        filePath = createImageUri()

        if (filePath != null) {
            // Jalankan kamera dengan membawa URI tempat menaruh hasilnya nanti
            takePictureLauncher.launch(filePath)
        } else {
            Toast.makeText(this, "Gagal menyiapkan penyimpanan internal", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        Toast.makeText(
                this,
                "Anda tidak bisa menambahkan photo profile tanpa izin kamera",
                Toast.LENGTH_LONG,
            )
            .show()
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()
    }

    // --- FUNGSI HELPER BARU ---
    // Mengonversi data Bitmap dari kamera menjadi File sementara agar mendapatkan Uri resmi yang
    // bisa dibaca Firebase
    private fun createImageUri(): Uri? {
        return try {
            val imageFile = File(cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
