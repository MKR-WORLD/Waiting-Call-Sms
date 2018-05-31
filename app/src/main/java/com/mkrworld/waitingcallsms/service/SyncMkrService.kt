package com.mkrworld.waitingcallsms.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.service.SyncMkrAdapter

/**
 * Created by mkr on 10/4/18.
 */
class SyncMkrService : Service {

    companion object {
        val TAG : String = BuildConfig.BASE_TAG + ".SyncMkrService"
    }

    private var mSyncMkrAdapter : SyncMkrAdapter? = null
    private var mLock : Any = Any()

    constructor() : super() {

    }

    override fun onCreate() {
        super.onCreate()
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized(mLock) {
            if (mSyncMkrAdapter == null) {
                mSyncMkrAdapter = SyncMkrAdapter(applicationContext, true)
            }
        }

    }

    override fun onBind(p0 : Intent?) : IBinder {
        return mSyncMkrAdapter?.syncAdapterBinder ?: Binder()
    }

}