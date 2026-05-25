package com.nesher.waroongpintar.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.cashier.CashierActivity
import com.nesher.waroongpintar.databinding.ActivityMainBinding
import com.nesher.waroongpintar.databinding.ViewMenuTileBinding
import com.nesher.waroongpintar.orders.OrdersActivity
import com.nesher.waroongpintar.productcatalogue.ProductCatalogueActivity
import com.nesher.waroongpintar.profile.ProfileActivity
import com.nesher.waroongpintar.utils.SimpleRecyclerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: SimpleRecyclerAdapter<ModuleView>
    private val moduleList = arrayListOf<ModuleView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = sb.bottom)
            insets
        }

        setupLayout()
    }

    private fun setupLayout() {
        setupModuleView()
        setupMenuAdapter()
        binding.avatar.setOnClickListener { goToProfile() }
    }

    private fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setupMenuAdapter() {
        adapter =
            SimpleRecyclerAdapter<ModuleView>(moduleList, R.layout.view_menu_tile) { holder, item ->
                val itemBinding: ViewMenuTileBinding = holder.layoutBinding as ViewMenuTileBinding

                itemBinding.menuIcon.setImageDrawable(resources.getDrawable(item.moduleIcon))
                itemBinding.menuTitle.text = item.moduleName
                itemBinding.root.setOnClickListener {
                    val intent: Intent = when (item.moduleName) {
                        resources.getString(R.string.product_catalogue) ->
                            Intent(applicationContext, ProductCatalogueActivity::class.java)

                        resources.getString(R.string.point_of_sales) ->
                            Intent(applicationContext, CashierActivity::class.java)

                        resources.getString(R.string.orders) ->
                            Intent(applicationContext, OrdersActivity::class.java)

                        else -> Intent(applicationContext, MainActivity::class.java)
                    }
                    startActivity(intent)
                }
            }
        binding.rvMenu.adapter = adapter
        binding.rvMenu.isNestedScrollingEnabled = false
    }

    private fun setupModuleView() {
        val productCatalogueModuleView =
            ModuleView(resources.getString(R.string.product_catalogue), R.drawable.ic_catalogue)
        val cashierModuleView =
            ModuleView(resources.getString(R.string.point_of_sales), R.drawable.ic_pos)
        val ordersModuleView =
            ModuleView(resources.getString(R.string.orders), R.drawable.ic_orders_list)

        moduleList.add(productCatalogueModuleView)
        moduleList.add(cashierModuleView)
        moduleList.add(ordersModuleView)
    }
}