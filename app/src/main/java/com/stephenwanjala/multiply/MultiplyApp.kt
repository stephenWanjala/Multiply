package com.stephenwanjala.multiply

import android.app.Application
import com.stephenwanjala.multiply.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin


class MultiplyApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MultiplyApp)
            modules(appModule)
        }
    }
}