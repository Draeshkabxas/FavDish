package com.derar.libya.favdish.model.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.derar.libya.favdish.model.*


const val FAV_DISH_TABLE_NAME: String = "fav_dishes_table"
const val IMAGE_SOURCE_NAME: String = "image_source"
const val COOKING_TIME_NAME: String = "cooking_time"
const val DIRECTION_TO_COOK_NAME: String = "instructions"
const val FAVORITE_DISH_NAME: String = "favorite_dish"
@Entity(tableName = FAV_DISH_TABLE_NAME)
data class FavDish(
    @ColumnInfo() val image : String,
    @ColumnInfo(name = IMAGE_SOURCE_NAME) val imageSource : String,
    @ColumnInfo() val title: String,
    @ColumnInfo() val type : String,
    @ColumnInfo() val category: String,
    @ColumnInfo() val ingredients: String,
    @ColumnInfo(name = COOKING_TIME_NAME) val cookingTime : String,
    @ColumnInfo(name = DIRECTION_TO_COOK_NAME) val directionToCook: String,
    @ColumnInfo(name = FAVORITE_DISH_NAME) var favoriteDish: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id:Int = 0
    )