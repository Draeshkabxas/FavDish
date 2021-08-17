package com.derar.libya.favdish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.derar.libya.favdish.model.dao.FavDishDao
import com.derar.libya.favdish.model.entities.FavDish


@Database(entities = arrayOf(FavDish::class),version = 1,exportSchema = false)
abstract class FavDishRoomDatabase: RoomDatabase() {

    abstract fun favDishDao(): FavDishDao

    companion object{

        @Volatile
        private var INSTANCE: FavDishRoomDatabase? = null

        fun getDatabase(context: Context): FavDishRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDatabase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

    }
}