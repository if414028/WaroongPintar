package com.nesher.waroongpintar.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nesher.waroongpintar.network.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    val email = MutableLiveData("")        // isi dari EditText (2-way binding)
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
                error.postValue(res.exceptionOrNull()?.localizedMessage ?: "Login gagal")
            }
        }
    }
}