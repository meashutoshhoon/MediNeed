package com.jb.medineed.app.di

import androidx.room.Room
import com.jb.medineed.app.data.local.MediStockDatabase
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.ui.page.entry.MedicineEntryViewModel
import com.jb.medineed.app.ui.page.lowstock.LowStockViewModel
import com.jb.medineed.app.ui.page.outofstock.OutOfStockViewModel
import com.jb.medineed.app.ui.page.reports.ReportsViewModel
import com.jb.medineed.app.ui.page.stock.StockListViewModel
import com.jb.medineed.app.ui.page.update.StockUpdateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MediStockDatabase::class.java,
            MediStockDatabase.DATABASE_NAME
        ).build()
    }
    single { get<MediStockDatabase>().medicineDao() }
    single { get<MediStockDatabase>().stockTransactionDao() }
}

val repositoryModule = module {
    single { MedicineRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { MedicineEntryViewModel(get()) }
    viewModel { StockListViewModel(get()) }
    viewModel { LowStockViewModel(get()) }
    viewModel { OutOfStockViewModel(get()) }
    viewModel { ReportsViewModel(get(), androidContext()) }
    viewModel { (medicineId: Long) -> StockUpdateViewModel(get(), medicineId) }
}

val appModules = listOf(databaseModule, repositoryModule, viewModelModule)