package com.example.freshfleet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItem::class, FavoriteItem::class], version = 1 , exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            if(INSTANCE == null) {
              synchronized(this) {
                  INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java ,
                      "appDB").build()
              }
            }
            return INSTANCE!!
        }
    }
}
