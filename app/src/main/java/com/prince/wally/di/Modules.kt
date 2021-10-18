package com.prince.wally.di

import android.content.Context.MODE_PRIVATE
import com.prince.wally.model.local.FavDatabase
import com.prince.wally.model.remote.ApiClient
import com.prince.wally.repository.Repository
import com.prince.wally.ui.activity.categories.CategoriesViewModel
import com.prince.wally.ui.activity.detail.DetailViewModel
import com.prince.wally.ui.activity.favorites.FavoriteViewModel
import com.prince.wally.ui.activity.videos.VideosViewModel
import com.prince.wally.ui.fragment.GalleryViewModel
import com.prince.wally.util.Constants.MAIN_STORAGE
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module { single { ApiClient.create(get()) } }

val dbModule = module { single { FavDatabase.buildDefault(get()).dao() } }

val repositoryModule = module { single { Repository(get(), get()) } }

val preferenceModule = module {
    single { androidApplication().getSharedPreferences(MAIN_STORAGE, MODE_PRIVATE) }
}

val viewModelModule = module(override = true) {
    viewModel { GalleryViewModel(get()) }
    viewModel { CategoriesViewModel(androidApplication(), get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { VideosViewModel(get()) }
}