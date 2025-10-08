package com.nesher.waroongpintar.productcatalogue

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.databinding.ActivityBulkUploadBinding

class BulkUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBulkUploadBinding
    private lateinit var viewModel: BulkUploadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[BulkUploadViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bulk_upload)

        setupLayout()
        observeVm()
    }

    private fun setupLayout() {
        binding.icBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val rules = resources.getStringArray(R.array.rules)
            .joinToString(separator = "\n") { "• $it" }
        binding.tvRules.text = rules

        val tips = resources.getStringArray(R.array.tips)
            .mapIndexed { i, s -> "${i + 1}. $s" }
            .joinToString("\n")
        binding.tvTips.text = tips
    }

    private fun observeVm() {

    }
}