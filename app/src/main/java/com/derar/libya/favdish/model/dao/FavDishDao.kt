package com.derar.libya.favdish.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.derar.libya.favdish.model.entities.FAV_DISH_TABLE_NAME
import com.derar.libya.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow


@Dao
interface FavDishDao {


    @Query("SELECT * FROM $FAV_DISH_TABLE_NAME ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>


    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)




}