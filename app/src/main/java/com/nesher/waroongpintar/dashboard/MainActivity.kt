package com.nesher.waroongpintar.dashboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupLayout()
    }

    private fun setupLayout() {
        setupRevenueMetric()
        setupOrdersMetric()
        setupProductsMetric()
    }

    private fun setupRevenueMetric() {
        binding.cardRevenue.metricTitle.text = getString(R.string.total_revenue)
        binding.cardRevenue.metricIcon.setImageResource(R.drawable.ic_rupiah)
    }

    private fun setupOrdersMetric() {
        binding.cardOrders.metricTitle.text = getString(R.string.total_orders)
        binding.cardOrders.metricIcon.setImageResource(R.drawable.ic_order)
    }

    private fun setupProductsMetric() {
        binding.cardProducts.metricTitle.text = getString(R.string.total_products)
        binding.cardProducts.metricIcon.setImageResource(R.drawable.ic_box)
    }
}