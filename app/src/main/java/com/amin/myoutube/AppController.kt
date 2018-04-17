package com.amin.myoutube

import android.support.multidex.MultiDexApplication

class AppController: MultiDexApplication() {
    companion object {
        lateinit var appController: AppController
    }

    override fun onCreate() {
        super.onCreate()
        appController = this
    }
}

fun AppController.Companion.getInstance(): AppController {
    return appController
}