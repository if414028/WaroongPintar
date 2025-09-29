package com.nesher.waroongpintar.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nesher.waroongpintar.App
import com.nesher.waroongpintar.network.AuthRepository
import kotlinx.coroutines.launch

data class ProfileUi(
    val fullName: String,
    val email: String,
    val phone: String,
    val storeName: String
)

class ProfileViewModel(private val repo: AuthRepository = AuthRepository((App.instance).supabase)) :
    ViewModel() {

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    val success = MutableLiveData(false)

    val profile = MutableLiveData<ProfileUi?>()

    fun onLogoutClicked() {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.signOut()
            if (res.isSuccess) {
                loading.postValue(false)
                success.postValue(true)
            } else {
                loading.postValue(false)
                val msg = res.exceptionOrNull()?.localizedMessage ?: "Gagal logout, coba lagi"
                error.postValue(msg)
            }
        }
    }

    /** panggil setelah UI menangani navigasi agar tidak double-emit di rotasi layar */
    fun consumeSuccess() {
        success.value = false
    }

    fun loadProfile() {
        loading.value = true
        error.value = null
        viewModelScope.launch {
            val res = repo.fetchMyProfileWithStore()
            if (res.isSuccess) {
                val dto = res.getOrNull()!!
                val ui = ProfileUi(
                    fullName = dto.userFullname ?: dto.userName ?: "-",
                    email = dto.email ?: "-",
                    phone = dto.phone ?: "-",
                    storeName = dto.primaryStore?.storeName ?: "-"
                )
                profile.postValue(ui)
                loading.postValue(false)
            } else {
                loading.postValue(false)
                error.postValue(res.exceptionOrNull()?.localizedMessage ?: "Gagal memuat profil")
            }
        }
    }
}