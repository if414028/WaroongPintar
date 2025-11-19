package com.nesher.waroongpintar.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nesher.waroongpintar.App
import com.nesher.waroongpintar.data.model.Profile
import com.nesher.waroongpintar.data.model.Subscription
import com.nesher.waroongpintar.network.AuthRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToLong

data class ProfileUi(
    val fullName: String,
    val email: String,
    val phone: String,
    val storeName: String
)

data class SubscriptionUi(
    val statusText: String,
    val planLabel: String,
    val nextBillingLabel: String
)

class ProfileViewModel(private val repo: AuthRepository = AuthRepository((App.instance).supabase)) :
    ViewModel() {

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    val success = MutableLiveData(false)

    val profile = MutableLiveData<ProfileUi?>()
    val subscription = MutableLiveData<SubscriptionUi?>()

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
                val dto = res.getOrNull()
                profile.postValue(dto?.toUi())
                loading.postValue(false)

                val storeId = dto?.primaryStore?.id
                if (!storeId.isNullOrBlank()) {
                    loadSubscription(storeId)
                } else {
                    subscription.postValue(null)
                }
            } else {
                loading.postValue(false)
                error.postValue(res.exceptionOrNull()?.localizedMessage ?: "Gagal memuat profil")
            }
        }
    }

    private fun loadSubscription(storeId: String) {
        viewModelScope.launch {
            val res = repo.fetchActiveSubscriptionForStore(storeId)
            if (res.isSuccess) {
                val sub = res.getOrNull()
                subscription.postValue(sub?.toUi() ?: SubscriptionUi(
                    statusText = "Belum berlangganan",
                    planLabel = "Gratis",
                    nextBillingLabel = "-"
                ))
            } else {
                subscription.postValue(SubscriptionUi(
                    statusText = "Tidak diketahui",
                    planLabel = "-",
                    nextBillingLabel = "-"
                ))
            }
        }
    }

    private fun Profile.toUi(): ProfileUi =
        ProfileUi(
            fullName  = userFullname ?: userName ?: "-",
            email     = email ?: "-",
            phone     = phone ?: "-",
            storeName = primaryStore?.storeName ?: "-"
        )

    private fun Subscription.toUi(): SubscriptionUi {
        val statusText = when (status?.lowercase()) {
            "active" -> "Subscription Aktif"
            "paused" -> "Subscription Dijeda"
            "canceled" -> "Subscription Berakhir"
            else -> "Subscription"
        }
        val planLabel = plans?.priceMonthly.toIdrPerMonthShort(plans?.planName)
        val nextBillingLabel = currentPeriodEnd.formatIndoDate()
        return SubscriptionUi(
            statusText = statusText,
            planLabel = planLabel,
            nextBillingLabel = nextBillingLabel
        )
    }

    private fun Double?.toIdrPerMonthShort(planName: String?): String {
        val v = this ?: return planName ?: "-"
        val rupiah = v.roundToLong()
        val k = rupiah / 1_000
        val label = if (k >= 1000) "${k / 1000}M" else "${k}K"
        return "Rp$label/bulan"
    }

    private fun String?.formatIndoDate(): String {
        if (this.isNullOrBlank()) return "-"
        val localeId = Locale("id", "ID")
        // coba beberapa parser umum
        val date: LocalDate? = try {
            // 2025-09-30
            LocalDate.parse(this)
        } catch (_: Exception) {
            try {
                // 2025-09-30T00:00:00Z / +07:00
                Instant.parse(this).atZone(ZoneId.of("Asia/Jakarta")).toLocalDate()
            } catch (_: Exception) {
                try {
                    OffsetDateTime.parse(this).toLocalDate()
                } catch (_: Exception) {
                    null
                }
            }
        }
        return date?.format(DateTimeFormatter.ofPattern("d MMM yyyy", localeId)) ?: "-"
    }
}