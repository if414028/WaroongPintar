package com.nesher.waroongpintar.di

import android.content.Context
import com.nesher.waroongpintar.AppDatabase
import com.nesher.waroongpintar.data.ProductRepository
import com.nesher.waroongpintar.data.dao.ProductBrandDao
import com.nesher.waroongpintar.data.dao.ProductCategoryDao
import com.nesher.waroongpintar.data.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.get(context)

    // (opsional) kalau mau inject DAO langsung
    @Provides fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
    @Provides fun provideCategoryDao(db: AppDatabase): ProductCategoryDao = db.categoryDao()
    @Provides fun provideBrandDao(db: AppDatabase): ProductBrandDao = db.brandDao()

    @Provides @Singleton
    fun provideProductRepository(db: AppDatabase): ProductRepository =
        ProductRepository(db)
}