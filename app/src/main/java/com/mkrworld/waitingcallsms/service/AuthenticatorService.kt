package com.mkrworld.waitingcallsms.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by mkr on 10/4/18.
 */
class AuthenticatorService : Service() {

    private var mAuthenticator: Authenticator? = null

    override fun onCreate() {
        super.onCreate()
        mAuthenticator = Authenticator(this);
    }

    override fun onBind(p0: Intent?): IBinder {
        return mAuthenticator!!.getIBinder();
    }

}