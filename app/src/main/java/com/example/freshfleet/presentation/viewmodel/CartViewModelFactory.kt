package com.example.freshfleet.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.repository.FavRepository
import com.example.freshfleet.data.repository.ShopCartRepository

class CartViewModelFactory(
    private val cartRepository: ShopCartRepository,
    private val favRepository: FavRepository?,
    val database: AppDatabase,
    val context: Context?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopCartViewModel::class.java)) {
            return ShopCartViewModel(cartRepository) as T
        } else if (modelClass.isAssignableFrom(FavViewModel::class.java)) {
            return favRepository?.let { FavViewModel(it) } as T
        } else if (context != null && modelClass.isAssignableFrom(ProductMainViewModel::class.java)) {
            return ProductMainViewModel(database, context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
