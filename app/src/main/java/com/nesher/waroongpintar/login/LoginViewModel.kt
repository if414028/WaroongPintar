package com.nesher.waroongpintar.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nesher.waroongpintar.App
import com.nesher.waroongpintar.network.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository = AuthRepository((App.instance).supabase)) :
    ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    val success = MutableLiveData(false)

    fun onLoginClicked() {
        val e = email.value?.trim().orEmpty()
        val p = password.value.orEmpty()

        if (e.isBlank() || p.isBlank()) {
            error.value = "Email & password wajib diisi"
            return
        }

        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.signIn(e, p)
            loading.postValue(false)

            if (res.isSuccess) {
                success.postValue(true)
            } else {
                val throwable = res.exceptionOrNull()
                val message = when {
                    throwable?.message?.contains("Invalid login credentials", true) == true -> {
                        "Username atau password salah"
                    }

                    throwable?.message?.contains("Network", true) == true -> {
                        "Koneksi internet bermasalah"
                    }

                    else -> {
                        "Login gagal, coba lagi"
                    }
                }
                error.postValue(message)
            }
        }
    }
}