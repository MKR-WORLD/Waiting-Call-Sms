package com.mkrworld.waitingcallsms.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle
import com.mkrworld.waitingcallsms.BuildConfig

/**
 * Created by mkr on 10/4/18.
 */
class Authenticator : AbstractAccountAuthenticator {

    companion object {
        val TAG: String = BuildConfig.BASE_TAG + ".Authenticator"
    }

    constructor(context: Context) : super(context) {

    }

    override fun getAuthTokenLabel(p0: String?): String {
        throw UnsupportedOperationException()
    }

    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?): Bundle? {
        return null
    }

    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?, p3: Array<out String>?, p4: Bundle?): Bundle? {
        return null
    }
}