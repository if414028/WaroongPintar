package com.nesher.waroongpintar.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nesher.waroongpintar.App
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.dashboard.MainActivity
import com.nesher.waroongpintar.databinding.ActivitySplashScreenBinding
import com.nesher.waroongpintar.login.LoginActivity
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val supabase by lazy { (application as App).supabase }

    @Volatile
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = sb.bottom)
            insets
        }

        lifecycleScope.launch {
            val dest = withContext(Dispatchers.IO) {
                // 1) cek ada session lokal?
                val session = supabase.auth.currentSessionOrNull()
                if (session == null) return@withContext Dest.Login

                // 2) verifikasi ke server (butuh JWT)
                val ok = runCatching {
                    supabase.auth.retrieveUser(session.accessToken)
                }.isSuccess

                if (ok) Dest.Dashboard else Dest.Login
            }
            navigate(dest)
        }
    }

    private fun navigate(dest: Dest) {
        if (navigated) return
        navigated = true

        val intent = when (dest) {
            Dest.Dashboard -> Intent(this, MainActivity::class.java)
            Dest.Login -> Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private enum class Dest { Dashboard, Login }
}