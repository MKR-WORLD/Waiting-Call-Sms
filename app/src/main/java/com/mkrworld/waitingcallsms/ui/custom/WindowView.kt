package com.mkrworld.waitingcallsms.ui.custom

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer


abstract class WindowView {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".WindowView"
    }

    protected val context: Context
    protected val windowManager: WindowManager

    /**
     * Constructor
     *
     * @param context
     * @param windowManager
     */
    constructor(context: Context, windowManager: WindowManager) {
        Tracer.debug(TAG, "constructor: ")
        this.context = context
        this.windowManager = windowManager
    }

    /**
     * Method to attach View with window
     */
    fun attachView() {
        Tracer.debug(TAG, "attachView: ")
        if (getWindowView() != null && getWindowView()!!.getParent() == null) {
            windowManager.addView(getWindowView(), getWindowManagerLayoutParam())
        }
    }

    /**
     * Method to detach View with window
     */
    fun detachView() {
        Tracer.debug(TAG, "detachView: ")
        windowManager.removeView(getWindowView())
    }

    /**
     * Method to get the view
     *
     * @return
     */
    abstract fun getWindowView(): View?

    /**
     * Method to get the layout param for window view
     *
     * @return
     */
    abstract fun getWindowManagerLayoutParam(): WindowManager.LayoutParams

}