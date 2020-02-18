package ru.tohaman.mywearapp

import android.app.Application
import ru.tohaman.mywearapp.dataSource.Domain
import timber.log.Timber

class MusicApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //Если приложение в дебаг версии, то выводим логи, если release, то не выводим
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Domain.integrateWith(this)
    }
}