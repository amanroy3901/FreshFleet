package com.example.freshfleet.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Upsert
    fun upsertFavItem(favouriteItem: FavoriteItem)

    @Delete
    fun deleteFavItem(favouriteItem: FavoriteItem)

    @Query("SELECT * FROM FavoriteItem")
    fun getAllFavoriteItems(): Flow<MutableList<FavoriteItem>>

//    @Query("SELECT * FROM FavoriteItem WHERE itemId = :itemId")
//    suspend fun getFavoriteItemById(itemId: Int): FavoriteItem

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(favoriteItem: FavoriteItem)
//
//    @Update
//    suspend fun update(favoriteItem: FavoriteItem)
//
//    @Delete
//    suspend fun delete(favoriteItem: FavoriteItem)
//
//    @Query("SELECT * FROM FavoriteItem")
//    suspend fun getAllFavoriteItems(): Flow<MutableList<FavoriteItem>>

//    @Query("SELECT * FROM FavoriteItem WHERE itemId = :itemId")
//    suspend fun getFavoriteItemById(itemId: Int): FavoriteItem?
}