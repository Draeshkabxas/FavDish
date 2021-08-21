package com.derar.libya.favdish.viewmodel

import androidx.lifecycle.*
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.model.repository.FavDishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavDishViewModel(
    private val repository: FavDishRepository
): ViewModel() {


    fun insert(dish: FavDish) =
        viewModelScope.launch{
            repository.insertFavDishData(dish)
        }

    val allDishesList: LiveData<List<FavDish>> =
        repository.allDishesList.asLiveData()

    fun update(dish:FavDish) =
        viewModelScope.launch{
            repository.updateFavDishData(dish)
        }

    val favoriteDishesList: LiveData<List<FavDish>> =
        repository.favoriteDishesList.asLiveData()


}


class FavDishViewModelFactory(
    private val repository: FavDishRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")

    }

}
