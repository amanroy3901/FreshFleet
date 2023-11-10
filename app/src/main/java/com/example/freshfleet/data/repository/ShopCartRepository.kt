package com.example.freshfleet.data.repository

import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.local.CartItem
import kotlinx.coroutines.flow.Flow

class ShopCartRepository(private val database: AppDatabase) {

    fun getAllCartItems(): Flow<MutableList<CartItem>> {
        return database.cartDao().getAllCartItems()
    }
}



