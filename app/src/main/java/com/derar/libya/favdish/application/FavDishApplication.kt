package com.derar.libya.favdish.application

import android.app.Application
import com.derar.libya.favdish.model.database.FavDishRoomDatabase
import com.derar.libya.favdish.model.repository.FavDishRepository

class FavDishApplication: Application() {

    private val database by lazy {
        FavDishRoomDatabase.getDatabase(
            this@FavDishApplication
        )
    }

    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }

}