package com.mkrworld.waitingcallsms.utils

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.mkrworld.androidlib.utils.MKRDialogUtil
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.R

class Utils {
    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".Utils"

        /**
         * Method to get the detail of the Phone number
         */
        fun getPhoneNumberDetail(context: Context, phoneNumber: String): Phonenumber.PhoneNumber? {
            try {
                val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
                val curLocale: String = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso.toUpperCase()
                val parse: Phonenumber.PhoneNumber = phoneNumberUtil.parse(phoneNumber, curLocale)
                return parse
            } catch (e: Exception) {
                Tracer.error(TAG, "getPhoneNumberDetail: " + e.message)
                return null
            }
        }

        /**
         * Method to show the Loading Dialog
         *
         * @param context
         */
        fun showLoadingDialog(context: Context) {
            Tracer.debug(TAG, "showLoadingDialog : ")
            var layoutInflater: LayoutInflater = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)!! as LayoutInflater);
            var view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            MKRDialogUtil.showLoadingDialog(context, view!!)
        }

        /**
         * Method to dismiss the Loading Dialog
         */
        fun dismissLoadingDialog() {
            Tracer.debug(TAG, "dismissLoadingDialog : ")
            MKRDialogUtil.dismissLoadingDialog()
        }

        /**
         * Method to schedule the Sync service
         * @param context
         */
        fun scheduleSyncService(context: Context) {
            Tracer.debug(TAG, "scheduleSyncService : ")
            val account: Account = getSyncAccount(context)
            ContentResolver.setMasterSyncAutomatically(true)
            ContentResolver.setSyncAutomatically(account, context.getString(R.string.sync_firebase_authority), true)
            ContentResolver.addPeriodicSync(account, context.getString(R.string.sync_firebase_authority), Bundle.EMPTY, 3600L)
            syncData(context)
        }

        /**
         * Method to schedule the Sync service
         * @param context
         */
        fun syncData(context: Context) {
            Tracer.debug(TAG, "syncData : ")
            val account: Account = getSyncAccount(context)
            ContentResolver.requestSync(account, context.getString(R.string.sync_firebase_authority), Bundle.EMPTY)
        }

        /**
         * Method to get the Account
         */
        @SuppressLint("MissingPermission")
        private fun getSyncAccount(context: Context): Account {
            val accountType: String = context.getString(R.string.sync_account_type).trim()
            val account: String = context.getString(R.string.sync_firebase_account).trim()
            val newAccount = Account(account, accountType)
            val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
            accountManager.addAccountExplicitly(newAccount, null, null)
            return newAccount
        }
    }
}
