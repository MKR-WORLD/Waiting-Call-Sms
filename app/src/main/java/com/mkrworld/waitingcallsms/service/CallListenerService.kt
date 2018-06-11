package com.mkrworld.waitingcallsms.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.db.DataBase
import com.mkrworld.waitingcallsms.ui.custom.WindowView
import com.mkrworld.waitingcallsms.utils.Tracer
import com.mkrworld.waitingcallsms.utils.Utils
import java.lang.reflect.Method

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
    inner class ListenPhoneState : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            Tracer.error(TAG, "onCallStateChanged: IDLE = 0, RINGING = 1, OFFHOOK = 2    STATE = $state      NUMBER = $incomingNumber")
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        val phoneNumberDetail = Utils.getPhoneNumberDetail(applicationContext, incomingNumber)
                        if (phoneNumberDetail != null && DataBase.getInstance(applicationContext).isContainNumber("" + phoneNumberDetail.countryCode, "" + phoneNumberDetail.nationalNumber)) {
                            val clazz = Class.forName(telephonyManager!!::class.qualifiedName)
                            val method: Method = clazz.getDeclaredMethod("getITelephony")
                            method.setAccessible(true)
                            val telephonyService: Any = method.invoke(telephonyManager)
                            val telephonyInterfaceClass = Class.forName(telephonyService::class.qualifiedName)
                            val methodEndCall: Method = telephonyInterfaceClass.getDeclaredMethod("endCall")
                            methodEndCall.invoke(telephonyInterfaceClass)
                            Tracer.error(TAG, "onCallStateChanged: END CALL:   NUMBER = $incomingNumber")
                            // CANCEL CALL AND SEND SMS
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Tracer.error(TAG, "onCallStateChanged: " + e.message)
                    }
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {

                }
                TelephonyManager.CALL_STATE_IDLE -> {

                }
            }
        }

    }
}
