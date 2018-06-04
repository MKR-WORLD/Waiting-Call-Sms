package com.mkrworld.waitingcallsms.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer
import android.telephony.TelephonyManager
import com.mkrworld.waitingcallsms.ui.custom.WindowView

class CallListenerService : Service() {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".CallListenerService"
    }

    private var listenPhoneState: ListenPhoneState? = null
    private var telephonyManager: TelephonyManager? = null
    private var windowView: WindowView? = null

    override fun onCreate() {
        super.onCreate()
        Tracer.debug(TAG, "onCreate: ")
        listenPhoneState = ListenPhoneState()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager?.listen(listenPhoneState, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Tracer.debug(TAG, "onStartCommand: ")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Tracer.debug(TAG, "onDestroy: ")
        telephonyManager?.listen(listenPhoneState, PhoneStateListener.LISTEN_NONE);
        super.onDestroy()
    }

    /**
     * Class to handle the Phone State Event when An Call is Received
     */
    class ListenPhoneState : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            Tracer.error(TAG, "onCallStateChanged: IDLE = 0, RINGING = 1, OFFHOOK = 2    STATE = $state      NUMBER = $incomingNumber")
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    // CANCEL CALL AND SEND SMS
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {

                }
                TelephonyManager.CALL_STATE_IDLE -> {

                }
            }
        }
    }
}
