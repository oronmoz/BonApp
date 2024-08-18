package com.example.bonapp

import android.app.Application
import com.example.bonapp.data.remote.initializeFirebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BonAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebase(this)
        Timber.plant(Timber.DebugTree())

    }
}