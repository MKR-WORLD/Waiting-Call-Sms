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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.mkrworld.androidlib.callback.OnBaseActivityListener
import com.mkrworld.androidlib.callback.OnBaseFragmentListener
import com.mkrworld.androidlib.controller.AppPermissionController
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.R
import com.mkrworld.waitingcallsms.db.DataBase
import com.mkrworld.waitingcallsms.db.TableContactInfo
import com.mkrworld.waitingcallsms.dto.DTOMKRTab
import com.mkrworld.waitingcallsms.provider.FragmentProvider
import com.mkrworld.waitingcallsms.service.CallListenerService
import com.mkrworld.waitingcallsms.ui.adapter.MKRTabAdapter
import com.mkrworld.waitingcallsms.utils.Tracer
import com.mkrworld.waitingcallsms.utils.Utils


class MainActivity : AppCompatActivity(), AppPermissionController.OnAppPermissionControllerListener, OnBaseActivityListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MainActivity"
    }

    private var appPermissionController: AppPermissionController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracer.debug(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.activity_main_toolbar))
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
                Manifest.permission.CALL_PHONE,
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

    override fun onBackPressed() {
        Tracer.debug(TAG, "onBackPressed: ")
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.activity_main_fragment_container)
        if (fragment != null && fragment is OnBaseFragmentListener && (fragment as OnBaseFragmentListener).onBackPressed()) {
            return
        }
        super.onBackPressed()
        fragment = supportFragmentManager.findFragmentById(R.id.activity_main_fragment_container)
        if (fragment != null && fragment is OnBaseFragmentListener) {
            (fragment as OnBaseFragmentListener).onPopFromBackStack()
            return
        }
    }

    override fun onBaseActivitySetToolbar(toolbarLayout: View) {
        Tracer.debug(TAG, "onBaseActivitySetToolbar: ");
    }

    override fun onBaseActivityAddFragment(fragment: Fragment, bundle: Bundle?, isAddToBackStack: Boolean, tag: String) {
        Tracer.debug(TAG, "onBaseActivityAddFragment: ");
        onBaseActivityAddFragment(R.id.activity_main_fragment_container, fragment, bundle, isAddToBackStack, tag)
    }

    override fun onBaseActivityAddFragment(containerId: Int, fragment: Fragment, bundle: Bundle?, isAddToBackStack: Boolean, tag: String) {
        Tracer.debug(TAG, "onBaseActivityAddFragment: ");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val enterAnim1 = R.anim.slide_in_right
        val exitAnim1 = R.anim.slide_out_left
        val enterAnim2 = R.anim.slide_in_left
        val exitAnim2 = R.anim.slide_out_right
        fragmentTransaction.setCustomAnimations(enterAnim1, exitAnim1, enterAnim2, exitAnim2);
        val findFragmentByTag = supportFragmentManager.findFragmentByTag(tag)
        if (findFragmentByTag == null) {
            fragmentTransaction.add(containerId, fragment, tag)
            if (isAddToBackStack) {
                fragmentTransaction.addToBackStack(tag)
            }
            if (bundle != null) {
                fragment!!.arguments = bundle
            }
            fragmentTransaction.commit()
        } else {
            findFragmentByTag.arguments = Bundle()
            supportFragmentManager.popBackStack(tag, 0)
        }
    }

    override fun onBaseActivityReplaceFragment(fragment: Fragment, bundle: Bundle?, tag: String) {
        Tracer.debug(TAG, "onBaseActivityReplaceFragment: ");
        onBaseActivityReplaceFragment(R.id.activity_main_fragment_container, fragment, bundle, tag)
    }

    override fun onBaseActivityReplaceFragment(containerId: Int, fragment: Fragment, bundle: Bundle?, tag: String) {
        Tracer.debug(TAG, "onBaseActivityReplaceFragment: ");
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val enterAnim1 = R.anim.slide_in_right
        val exitAnim1 = R.anim.slide_out_left
        val enterAnim2 = R.anim.slide_in_left
        val exitAnim2 = R.anim.slide_out_right
        fragmentTransaction.setCustomAnimations(enterAnim1, exitAnim1, enterAnim2, exitAnim2);
        fragmentTransaction.replace(containerId, fragment, tag)
        if (bundle != null) {
            fragment!!.arguments = bundle
        }
        fragmentTransaction.commit()
    }

    override fun onBaseActivitySetScreenTitle(title: String) {
        Tracer.debug(TAG, "onBaseActivitySetScreenTitle: ");
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
                val contactName: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) ?: ""
                val contactId: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts._ID)) ?: ""
                val idResult: String = cursorId?.getString(cursorId?.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) ?: ""
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

    /**
     * Method to init the Activity
     */
    private fun init() {
        Tracer.debug(TAG, "init: ")
        startService(Intent(applicationContext, CallListenerService::class.java))
        //startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 7);
        val viewPager = findViewById<ViewPager>(R.id.activity_main_view_pager)
        var tabList: ArrayList<DTOMKRTab> = ArrayList<DTOMKRTab>()
        tabList.add(DTOMKRTab(FragmentProvider.FragmentTag.BLOCK_NUMBER_LIST, FragmentProvider.getLabel(this, FragmentProvider.FragmentTag.BLOCK_NUMBER_LIST.name)))
        tabList.add(DTOMKRTab(FragmentProvider.FragmentTag.FORM_BLOCK_NUMBER, FragmentProvider.getLabel(this, FragmentProvider.FragmentTag.FORM_BLOCK_NUMBER.name)))
        tabList.add(DTOMKRTab(FragmentProvider.FragmentTag.TEMPLATE, FragmentProvider.getLabel(this, FragmentProvider.FragmentTag.TEMPLATE.name)))
        viewPager.adapter = MKRTabAdapter(supportFragmentManager, tabList)
        if (viewPager.adapter is ViewPager.OnPageChangeListener) {
            viewPager.addOnPageChangeListener(viewPager.adapter as MKRTabAdapter)
        }
    }
}
