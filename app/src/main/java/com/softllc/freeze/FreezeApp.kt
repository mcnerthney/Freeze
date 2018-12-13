package com.softllc.freeze

import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FreezeApp : Application() {
    private val powerReceiver = BroadcastReceiver(this)

    override fun onCreate() {
        super.onCreate()

        val theFilter = IntentFilter()
        /** System Defined Broadcast  */
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_SCREEN_OFF)
        theFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(powerReceiver, theFilter)
    }


    class BroadcastReceiver ( private val app : FreezeApp ) : android.content.BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            try {
                val action = intent.action

                when (action) {
                    Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF, Intent.ACTION_USER_PRESENT -> {
                        val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                        if (myKM.inKeyguardRestrictedInputMode()) {
                            _locked.postValue(true)
                        } else {
                            _locked.postValue(false)
                        }

                    }
                }
            } catch (e: Exception) {

            }

        }
    }

    companion object {
        private var _locked = MutableLiveData<Boolean>()
        var locked : LiveData<Boolean> = _locked
    }

}
