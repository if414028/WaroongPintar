package com.nesher.waroongpintar.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = sb.bottom)
            insets
        }

        setupLayout()
        observeVm()
        loadProfile()
    }

    private fun loadProfile() {
        binding.isLoading = true
        binding.isError = false
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
            binding.isLoading = isLoading
            binding.isError = false
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { binding.tvErrorMessage.text = it }

            binding.isError = true
            binding.isLoading = false
        }

        viewModel.success.observe(this) { ok ->
            if (ok == true) {
                binding.isError = false
                binding.isLoading = false

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