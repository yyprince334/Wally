package com.prince.wally

import android.app.Application
import com.prince.wally.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(apiModule, dbModule, repositoryModule, preferenceModule, viewModelModule))
        }
    }
}