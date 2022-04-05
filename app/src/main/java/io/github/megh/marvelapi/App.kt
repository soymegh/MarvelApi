package io.github.megh.marvelapi

import android.app.Application
import android.content.Context
import coil.ImageLoader
import io.github.megh.marvelapi.common.CrashReportingTree
import timber.log.Timber

class App : Application(), ModuleOwner {

    override val appModule by lazy { AppModule.create(ImageLoader(this)) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())
    }
}

fun Context.appModule(): AppModule = (applicationContext as ModuleOwner).appModule