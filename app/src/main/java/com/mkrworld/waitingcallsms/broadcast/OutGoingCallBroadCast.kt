package com.mkrworld.waitingcallsms.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer

import com.mkrworld.waitingcallsms.utils.Utils

class OutGoingCallBroadCast : BroadcastReceiver() {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".OutGoingCallBroadCast"
    }


    override fun onReceive(context: Context, intent: Intent) {
        Tracer.debug(TAG, "onReceive: ")
    }
}
