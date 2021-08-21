package com.derar.libya.favdish.model.repository

import androidx.annotation.WorkerThread
import com.derar.libya.favdish.model.dao.FavDishDao
import com.derar.libya.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {


    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> =
        favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    val favoriteDishesList: Flow<List<FavDish>> =
        favDishDao.getFavoriteDishesList()

}