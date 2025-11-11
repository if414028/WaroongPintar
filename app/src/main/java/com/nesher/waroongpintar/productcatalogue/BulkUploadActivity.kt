package com.nesher.waroongpintar.productcatalogue

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.databinding.ActivityBulkUploadBinding
import com.nesher.waroongpintar.databinding.ItemPreviewRowBinding
import com.nesher.waroongpintar.utils.SimpleRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class BulkUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBulkUploadBinding
    private lateinit var viewModel: BulkUploadViewModel

    private lateinit var previewAdapter: SimpleRecyclerAdapter<BulkUploadViewModel.PreviewRow>

    private val pickCsv = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val text = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        if (text.isNullOrEmpty()) {
            Snackbar.make(binding.root, "Gagal membaca file", Snackbar.LENGTH_LONG).show()
        } else {
            viewModel.parseCsvText(text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[BulkUploadViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bulk_upload)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = sb.bottom)
            insets
        }

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

        binding.btnUploadCsv.setOnClickListener {
            val preview = viewModel.preview.value.orEmpty()
            if (preview.isEmpty()) {
                pickCsv.launch("text/*")
            } else {
                viewModel.importValidRows()
            }
        }
        setupReviewProductRecyclerView()
    }

    private val rupiahFmt: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }
    }

    private fun setupReviewProductRecyclerView() {

        previewAdapter = SimpleRecyclerAdapter(
            mainData = mutableListOf(),
            layoutRes = R.layout.item_preview_row
        ) { holder, row ->
            val itembinding: ItemPreviewRowBinding = holder.layoutBinding as ItemPreviewRowBinding
            val r = row.raw

            itembinding.tvName.text = r["name"].orEmpty()
            itembinding.tvCategory.text = r["category_name"].orEmpty()
            itembinding.tvPrice.text = r["price"]?.toLongOrNull()?.let { rupiahFmt.format(it) } ?: "-"
            itembinding.tvStock.text = r["stock"]?.toIntOrNull()?.toString() ?: "0"
            itembinding.tvUnit.text = r["unit"].orEmpty().ifEmpty { "pcs" }
            itembinding.tvBarcode.text = r["barcode"].orEmpty().ifEmpty { "—" }

            bindStatusChip(itembinding.chipStatus, row)
        }

        binding.rvPreview.apply {
            this.layoutManager = LinearLayoutManager(this@BulkUploadActivity)
            this.adapter = previewAdapter
            this.setHasFixedSize(false)
        }
    }

    private fun bindStatusChip(chip: Chip, row: BulkUploadViewModel.PreviewRow) {
        val ctx = chip.context
        if (row.isValid) {
            chip.text = "Valid"
            chip.setTextColor(ContextCompat.getColor(ctx, R.color.wp_success))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.wp_light_green)
            chip.chipStrokeColor     = ContextCompat.getColorStateList(ctx, R.color.wp_success)
        } else {
            val msg = row.errors.firstOrNull().orEmpty().ifEmpty { "Error" }
            chip.text = msg
            chip.setTextColor(ContextCompat.getColor(ctx, R.color.wp_danger))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(ctx, R.color.wp_light_red)
            chip.chipStrokeColor     = ContextCompat.getColorStateList(ctx, R.color.wp_danger)
        }
    }

    private fun moveChipToStartProgress() {
        binding.progressStart.setTextColor(ContextCompat.getColor(this, R.color.wp_info))
        binding.progressStart.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_light_blue)
        binding.progressStart.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_info)

        binding.progressReview.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressReview.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressReview.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)

        binding.progressDone.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressDone.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressDone.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)
    }

    private fun moveChipToReviewProgress() {
        binding.progressStart.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressStart.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressStart.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)

        binding.progressReview.setTextColor(ContextCompat.getColor(this, R.color.wp_info))
        binding.progressReview.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_light_blue)
        binding.progressReview.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_info)

        binding.progressDone.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressDone.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressDone.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)
    }

    private fun moveChipToDoneProgress() {
        binding.progressStart.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressStart.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressStart.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)

        binding.progressReview.setTextColor(ContextCompat.getColor(this, R.color.wp_stroke))
        binding.progressReview.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_chip_bg)
        binding.progressReview.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_stroke)

        binding.progressDone.setTextColor(ContextCompat.getColor(this, R.color.wp_info))
        binding.progressDone.chipBackgroundColor =
            ContextCompat.getColorStateList(this, R.color.wp_light_blue)
        binding.progressDone.chipStrokeColor =
            ContextCompat.getColorStateList(this, R.color.wp_info)
    }

    private fun observeVm() {
        viewModel.preview.observe(this) { rows ->
            val total = rows.size
            val valid = rows.count { it.isValid }
            val invalid = total - valid
            binding.tvReviewTitle.text = getString(R.string.review_data)
            if (total == 0) {
                moveChipToStartProgress()
                binding.tvReviewDesc.text = getString(R.string.review_data_desc)
            } else {
                moveChipToReviewProgress()
                binding.tvReviewDesc.text = buildString {
                    append("Total $total baris • Valid $valid • Error $invalid.\n")
                    append("Jika tidak ada error, klik tombol untuk impor.")
                }
            }

            if (total > 0) {
                binding.btnUploadCsv.text = getString(R.string.import_to_db)
                binding.btnUploadCsv.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_green_button)
                binding.rowHeader.visibility = View.VISIBLE
                previewAdapter.updateMainData(rows.toMutableList())
            } else {
                binding.btnUploadCsv.text = getString(R.string.choose_file)
                binding.btnUploadCsv.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_button_gradient_circle)
                binding.rowHeader.visibility = View.GONE
            }
        }

        viewModel.statusText.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.busy.observe(this) { busy ->
            binding.btnUploadCsv.isEnabled = !busy
            binding.btnUploadCsv.alpha = if (busy) 0.5f else 1f
        }

        viewModel.importResult.observe(this) { res ->
            res ?: return@observe
            Snackbar.make(binding.root, res.message, Snackbar.LENGTH_LONG).show()
        }
    }
}