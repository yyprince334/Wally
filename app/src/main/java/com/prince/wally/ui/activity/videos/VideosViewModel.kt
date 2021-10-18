package com.prince.wally.ui.activity.videos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prince.wally.model.remote.response.videos.Item
import com.prince.wally.repository.Repository
import kotlinx.coroutines.launch

class VideosViewModel(private val repository: Repository) : ViewModel() {

    val videos = MutableLiveData<List<Item>>()

    init {
        viewModelScope.launch {
            videos.postValue(repository.getVideos().body()?.items)
        }
    }
}