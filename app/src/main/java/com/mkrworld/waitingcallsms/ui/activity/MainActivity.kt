package com.mkrworld.waitingcallsms.ui.activity

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.mkrworld.androidlib.controller.AppPermissionController
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.R
import com.mkrworld.waitingcallsms.db.DataBase
import com.mkrworld.waitingcallsms.db.TableContactInfo
import com.mkrworld.waitingcallsms.service.CallListenerService
import com.mkrworld.waitingcallsms.utils.Tracer
import com.mkrworld.waitingcallsms.utils.Utils


class MainActivity : AppCompatActivity(), AppPermissionController.OnAppPermissionControllerListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MainActivity"
    }

    private var appPermissionController: AppPermissionController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracer.debug(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                finish()
                return
            }
        }
        val permissions: Array<String> = arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_SYNC_SETTINGS,
                Manifest.permission.WRITE_SYNC_SETTINGS,
                Manifest.permission.READ_SYNC_STATS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.GET_TASKS)
        appPermissionController = AppPermissionController(this, permissions, this)
        appPermissionController?.initializedAppPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Tracer.debug(TAG, "onRequestPermissionsResult: ")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appPermissionController?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAppPermissionControllerListenerHaveAllRequiredPermission() {
        Tracer.debug(TAG, "onAppPermissionControllerListenerHaveAllRequiredPermission: ")
        init()
    }

    /**
     * Method to init the Activity
     */
    private fun init() {
        Tracer.debug(TAG, "init: ")
        startService(Intent(applicationContext, CallListenerService::class.java))
        startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 7);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            7 -> {
                val uri: Uri? = data?.getData()
                if (uri == null) {
                    return
                }
                var cursorId: Cursor = contentResolver.query(uri, null, null, null, null)
                if (cursorId == null || cursorId.count == 0) {
                    return
                }
                // MOVE THE INDEX TOWARDS THE FIRST ITEM
                cursorId!!.moveToFirst()
                val contactName: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        ?: ""
                val contactId: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts._ID))
                        ?: ""
                val idResult: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        ?: ""
                if (Integer.valueOf(idResult) == 1) {
                    val cursorNumber: Cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
                    if (cursorNumber == null || cursorNumber.count == 0) {
                        return
                    }
                    while (cursorNumber?.moveToNext()) {
                        var phoneNumber: String = cursorNumber?.getString(cursorNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                ?: ""
                        val phoneNumberDetail = Utils.getPhoneNumberDetail(this, phoneNumber)
                        val contactInfo = TableContactInfo.ContactInfo()
                        contactInfo.countryCode = "" + phoneNumberDetail?.countryCode
                        contactInfo.number = "" + phoneNumberDetail?.nationalNumber
                        contactInfo.name = contactName
                        DataBase.getInstance(applicationContext).saveContactInfo(contactInfo)
                    }
                }
            }
        }
    }
}
