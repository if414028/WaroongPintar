package com.nesher.waroongpintar.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.databinding.ActivityProfileBinding
import com.nesher.waroongpintar.login.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this

        setupLayout()
        observeVm()
        viewModel.loadProfile()
    }

    private fun setupLayout() {
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Keluar dari aplikasi?")
                .setMessage("Anda akan keluar dan perlu login kembali.")
                .setPositiveButton("Keluar") { _, _ -> viewModel.onLogoutClicked() }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.icBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeVm() {
        viewModel.loading.observe(this) { isLoading ->
            binding.btnLogout.isEnabled = !isLoading
            // tampilkan progress jika punya
            // binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }

        viewModel.success.observe(this) { ok ->
            if (ok == true) {
                goToLoginAndClearBackstack()
                viewModel.consumeSuccess()
            }
        }

        viewModel.profile.observe(this) { p ->
            binding.tvStoreName.text = p?.storeName ?: "-"
            binding.tvEmail.text = p?.email ?: "-"
            binding.tvStoreNameValue.text = p?.storeName ?: "-"
            binding.tvOwnerNameValue.text = p?.fullName ?: "-"
            binding.tvPhoneValue.text = p?.phone ?: "-"
        }

        viewModel.subscription.observe(this) { s ->
            if (s != null) {
                binding.tvSubStatus.text = s.statusText
                binding.chipPlan.text = s.planLabel
                binding.tvNextBilling.text = "Berikutnya: ${s.nextBillingLabel}"
            } else {
                binding.tvSubStatus.text = "Belum berlangganan"
                binding.chipPlan.text = "Gratis"
                binding.tvNextBilling.text = "Berikutnya: -"
            }
        }
    }

    private fun goToLoginAndClearBackstack() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}