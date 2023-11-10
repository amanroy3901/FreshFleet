package com.example.freshfleet.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Upsert
    fun upsertCartItem(cartItem: CartItem)

    @Delete
    fun delete(cartItem: CartItem)

    @Query("SELECT * FROM CartItem")
    fun getAllCartItems(): Flow<MutableList<CartItem>>

//    @Query("SELECT * FROM CartItem WHERE itemId = :itemId")
//    suspend fun getCartItemById(itemId: Int): CartItem?
}