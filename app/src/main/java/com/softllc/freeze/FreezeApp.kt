package com.softllc.freeze

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class FreezeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //Fresco.initialize(applicationContext)
    }
}
