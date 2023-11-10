package com.example.freshfleet.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteItem")
data class FavoriteItem(
    @ColumnInfo(name = "image")
    val image: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    val itemId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "unit")
    var unit: Int,
    @ColumnInfo(name = "inCart")
    var inCart: Boolean
)