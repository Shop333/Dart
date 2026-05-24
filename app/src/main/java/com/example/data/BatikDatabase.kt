package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BatikProduct::class,
        CartItemEntity::class,
        UserProfile::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BatikDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: BatikDatabase? = null

        fun getDatabase(context: Context): BatikDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BatikDatabase::class.java,
                    "batik_store_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
