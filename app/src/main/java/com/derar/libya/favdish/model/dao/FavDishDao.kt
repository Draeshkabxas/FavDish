package com.derar.libya.favdish.model.dao

import androidx.room.*
import com.derar.libya.favdish.model.entities.FAVORITE_DISH_NAME
import com.derar.libya.favdish.model.entities.FAV_DISH_TABLE_NAME
import com.derar.libya.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow


@Dao
interface FavDishDao {


    @Query("SELECT * FROM $FAV_DISH_TABLE_NAME ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>


    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Update
    suspend fun updateFavDishDetails(favDish:FavDish)

    @Query("SELECT * FROM $FAV_DISH_TABLE_NAME WHERE $FAVORITE_DISH_NAME = 1")
    fun getFavoriteDishesList():Flow<List<FavDish>>

    @Delete
    suspend fun deleteFavDishDetails(favDish:FavDish)

    @Query("SELECT * FROM $FAV_DISH_TABLE_NAME WHERE type= :filterType")
     fun getFilterDishesList(filterType:String):Flow<List<FavDish>>

}