package com.mkrworld.waitingcallsms.service

import android.accounts.Account
import android.content.*
import android.os.Bundle
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer


/**
 * Created by mkr on 10/4/18.
 */
class SyncMkrAdapter : AbstractThreadedSyncAdapter {

    companion object {
        val TAG: String = BuildConfig.BASE_TAG + ".SyncMkrAdapter"
    }

    var mContentResolver: ContentResolver? = null

    constructor(context: Context, autoInitialize: Boolean) : super(context, autoInitialize) {
        Tracer.debug(TAG, "Constructor-2: ")
        init()
    }

    constructor(context: Context, autoInitialize: Boolean, allowParallelSyncs: Boolean) : super(context, autoInitialize, allowParallelSyncs) {
        Tracer.debug(TAG, "Constructor-3: ")
        init()
    }

    /**
     * Method to perform the actual Syncing
     * @param account An Account object associated with the event that triggered the sync adapter. If your server doesn't use accounts, you don't need to use the information in this object.
     * @param bundle A Bundle containing flags sent by the event that triggered the sync adapter.
     * @param authority The authority of a content provider in the system. Your app has to have access to this provider. Usually, the authority corresponds to a content provider in your own app.
     * @param contentProvider A ContentProviderClient for the content provider pointed to by the authority argument. A ContentProviderClient is a lightweight public interface to a content provider. It has the same basic functionality as a ContentResolver. If you're using a content provider to store data for your app, you can connect to the provider with this object. Otherwise, you can ignore it.
     * @param syncResult  A SyncResult object that you use to send information to the sync adapter framework.
     */
    override fun onPerformSync(account: Account?, bundle: Bundle?, authority: String?, contentProvider: ContentProviderClient?, syncResult: SyncResult?) {
        Tracer.debug(TAG, "onPerformSync: ")
        context.applicationContext.startService(Intent(context.applicationContext, CallListenerService::class.java))
    }

    /**
     * Method to initialize the Sync Adapter
     */
    private fun init() {
        mContentResolver = context.contentResolver
    }
}