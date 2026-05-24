package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class, LocationEntity::class], version = 1, exportSchema = false)
abstract class IkeaFoodDatabase : RoomDatabase() {
    abstract val dao: IkeaFoodDao

    companion object {
        @Volatile
        private var INSTANCE: IkeaFoodDatabase? = null

        fun getDatabase(context: Context): IkeaFoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IkeaFoodDatabase::class.java,
                    "ikea_food_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
