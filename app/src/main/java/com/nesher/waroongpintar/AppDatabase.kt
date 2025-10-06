package com.nesher.waroongpintar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nesher.waroongpintar.data.model.Product
import com.nesher.waroongpintar.data.model.ProductBrand
import com.nesher.waroongpintar.data.model.ProductCategory
import com.nesher.waroongpintar.data.model.ProductList
import com.nesher.waroongpintar.data.dao.ProductBrandDao
import com.nesher.waroongpintar.data.dao.ProductCategoryDao
import com.nesher.waroongpintar.data.dao.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [Product::class, ProductCategory::class, ProductBrand::class],
    views = [ProductList::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): ProductCategoryDao
    abstract fun brandDao(): ProductBrandDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waroong.db"
                )
                    // .addMigrations(MIGRATION_1_2, ...) // nanti kalau versi naik
                    .addCallback(SeedCallback)
                    .build()
            }.also { INSTANCE = it }

        private object SeedCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // seeding ringan (non-blocking) via coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    val database = INSTANCE ?: return@launch
                    val cat = listOf(
                        ProductCategory(id = "cat-a", name = "Kategori A"),
                        ProductCategory(id = "cat-b", name = "Kategori B")
                    )
                    val brand = listOf(
                        ProductBrand(id = "br-abc", name = "Brand ABC")
                    )
                    database.categoryDao().upsertAll(cat)
                    database.brandDao().upsertAll(brand)

                    val products = (1..30).map {
                        Product(
                            id = "prd-$it",
                            sku = "ABC$it",
                            name = "Produk ABC $it",
                            categoryId = if (it % 2 == 0) "cat-a" else "cat-b",
                            brandId = "br-abc",
                            stock = 16,
                            price = 24_000L,
                            imageUrl = null
                        )
                    }
                    database.productDao().upsertAll(products)
                }
            }
        }
    }
}