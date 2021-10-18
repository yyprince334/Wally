package com.prince.wally.ui.activity.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prince.wally.model.local.Favorite
import com.prince.wally.repository.Repository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: Repository) : ViewModel() {

    val favorites = MutableLiveData<List<Favorite>>()
    val isEmpty = MutableLiveData<Boolean>()

    fun getFavorites() {
        viewModelScope.launch {
            val favs = repository.getFavorites()
            favorites.postValue(favs)
            isEmpty.postValue(favs.isNullOrEmpty())
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            getFavorites()
        }
    }
}