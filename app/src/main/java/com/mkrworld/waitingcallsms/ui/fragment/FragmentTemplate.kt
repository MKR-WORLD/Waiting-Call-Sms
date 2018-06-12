package com.mkrworld.waitingcallsms.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mkrworld.androidlib.callback.OnBaseFragmentListener
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.R
import com.mkrworld.waitingcallsms.utils.Tracer

class FragmentTemplate : Fragment(), OnBaseFragmentListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".FragmentTemplate"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Tracer.debug(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Tracer.debug(TAG, "onViewCreated: ")
    }

    override fun onBackPressed(): Boolean {
        Tracer.debug(TAG, "onBackPressed: ")
        return false
    }

    override fun onNotifyFragment(flag: Int, bundle: Bundle) {
        Tracer.debug(TAG, "onNotifyFragment: ")
    }

    override fun onPermissionsResult(requestCode: Int, permissions: Array<String>?, grantResults: IntArray) {
        Tracer.debug(TAG, "onPermissionsResult: ")
    }

    override fun onPopFromBackStack() {
        Tracer.debug(TAG, "onPopFromBackStack: ")
    }

    override fun onRefresh() {
        Tracer.debug(TAG, "onRefresh: ")
    }
}