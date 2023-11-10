package com.example.freshfleet.data.repository

import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.local.FavoriteItem
import kotlinx.coroutines.flow.Flow

class FavRepository(private val database: AppDatabase) {

    fun getAllFavoriteItems(): Flow<MutableList<FavoriteItem>> {
        return database.favoriteDao().getAllFavoriteItems()
    }
}