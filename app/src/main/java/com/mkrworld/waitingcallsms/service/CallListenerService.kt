package com.mkrworld.waitingcallsms.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
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
                            val serviceManagerName = "android.os.ServiceManager"
                            val serviceManagerNativeName = "android.os.ServiceManagerNative"
                            val telephonyName = "com.android.internal.telephony.ITelephony"
                            val telephonyClass: Class<*>
                            val telephonyStubClass: Class<*>
                            val serviceManagerClass: Class<*>
                            val serviceManagerNativeClass: Class<*>
                            val telephonyEndCall: Method
                            val telephonyObject: Any
                            val serviceManagerObject: Any
                            telephonyClass = Class.forName(telephonyName)
                            telephonyStubClass = telephonyClass.classes[0]
                            serviceManagerClass = Class.forName(serviceManagerName)
                            serviceManagerNativeClass = Class.forName(serviceManagerNativeName)
                            val getService = serviceManagerClass.getMethod("getService", String::class.java)
                            val tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder::class.java)
                            val tmpBinder = Binder()
                            tmpBinder.attachInterface(null, "fake")
                            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder)
                            val retbinder = getService.invoke(serviceManagerObject, "phone") as IBinder
                            val serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder::class.java)
                            telephonyObject = serviceMethod.invoke(null, retbinder)
                            telephonyEndCall = telephonyClass.getMethod("endCall")
                            telephonyEndCall.invoke(telephonyObject)
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
