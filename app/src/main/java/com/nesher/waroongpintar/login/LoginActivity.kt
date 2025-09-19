package com.nesher.waroongpintar.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.dashboard.MainActivity
import com.nesher.waroongpintar.databinding.ActivityLoginBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    private var backPressedOnce = false
    private var backPressedToast: Toast? = null
    private var backPressJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        setupLayout()
        observeVm()
    }

    private fun observeVm() {
        viewModel.success.observe(this) { ok ->
            if (ok == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        viewModel.error.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
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
    }
}