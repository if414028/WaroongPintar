package com.nesher.waroongpintar.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.dashboard.MainActivity
import com.nesher.waroongpintar.databinding.ActivityLoginBinding
import com.nesher.waroongpintar.utils.DataStoreManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var dataStoreManager: DataStoreManager

    private var backPressedOnce = false
    private var backPressedToast: Toast? = null
    private var backPressJob: Job? = null

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        dataStoreManager = DataStoreManager(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        setupLayout()
        observeSavedData()
        observeVm()
    }

    private fun observeSavedData() {
        lifecycleScope.launch {
            dataStoreManager.getLogin.collect { (savedEmail, savedPassword, remember) ->
                binding.etUsername.setText(savedEmail)
                if (remember) {
                    binding.etPassword.setText(savedPassword)
                    binding.cbRemember.isChecked = true
                }
            }
        }
    }

    private fun observeVm() {
        viewModel.success.observe(this) { ok ->
            if (ok == true) {
                val email = binding.etUsername.text.toString()
                val password = binding.etPassword.text.toString()
                val remember = binding.cbRemember.isChecked

                lifecycleScope.launch {
                    try {
                        dataStoreManager.saveLogin(email, password, remember)
                        goToDahsboard()
                        finish()
                    } catch (t: Throwable) {
                        Log.e("Login", "Save login failed", t)
                    }

                }
            }
        }
        viewModel.error.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToDahsboard() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun setupLayout() {
        //callback when back pressed
        onBackPressedDispatcher.addCallback {
            if (backPressedOnce) {
                backPressedToast?.cancel()
                finishAffinity()
            } else {
                backPressedOnce = true
                backPressedToast = Toast.makeText(
                    this@LoginActivity,
                    "Tekan sekali lagi untuk keluar dari aplikasi",
                    Toast.LENGTH_SHORT
                )
                backPressedToast?.show()


                backPressJob?.cancel()
                backPressJob = lifecycleScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }
        setupPasswordToggle()
    }

    private fun setupPasswordToggle() {

        binding.icTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Show password
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.icTogglePassword.setImageResource(R.drawable.ic_view)
            } else {
                // Hide password
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.icTogglePassword.setImageResource(R.drawable.ic_hide)
            }
            // Set cursor to the end after toggling
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }
    }
}