package com.nesher.waroongpintar.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

private val rupiahFormatter: NumberFormat by lazy {
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        currency = Currency.getInstance("IDR")
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
}

/** Format Long (harga dalam rupiah) -> "Rp24.000" / "Rp 24.000" tergantung device */
fun Long.toRupiah(): String = rupiahFormatter.format(this)

/** Overload: Int */
fun Int.toRupiah(): String = this.toLong().toRupiah()

/** Overload: BigDecimal (pakai nilai dibulatkan ke rupiah) */
fun BigDecimal.toRupiah(): String = this.setScale(0, BigDecimal.ROUND_HALF_UP).toLong().toRupiah()

/** Overload: Long? aman null */
fun Long?.toRupiahOrDash(): String = this?.toRupiah() ?: "-"

/** (opsional) Hapus simbol mata uang, hanya angka berformat: "24.000" */
fun Long.toRupiahNumberOnly(): String =
    rupiahFormatter.format(this).replace(Regex("[^0-9.,]"), "").trim()