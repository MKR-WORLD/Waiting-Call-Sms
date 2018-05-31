package com.mkrworld.waitingcallsms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mkrworld.androidlib.controller.AppPermissionController
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Handler
import android.provider.Settings
import com.mkrworld.waitingcallsms.utils.Tracer
import com.mkrworld.waitingcallsms.utils.Utils

class MainActivity : AppCompatActivity(), AppPermissionController.OnAppPermissionControllerListener {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".MainActivity"
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
        Utils.showLoadingDialog(this)
        Handler().postDelayed(object : Runnable {
            override fun run() {
                Utils.dismissLoadingDialog()
            }
        }, 5000)
    }
}
