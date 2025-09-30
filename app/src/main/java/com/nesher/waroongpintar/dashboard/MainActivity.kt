package com.nesher.waroongpintar.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupLayout()
    }

    private fun setupLayout() {
        setupModuleView()
        setupRevenueMetric()
        setupOrdersMetric()
        setupProductsMetric()
        setupMenuAdapter()
        binding.avatar.setOnClickListener { goToProfile() }
    }

    private fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
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

    private fun setupMenuAdapter() {
        binding.rvMenu.layoutManager = GridLayoutManager(applicationContext, 6)
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