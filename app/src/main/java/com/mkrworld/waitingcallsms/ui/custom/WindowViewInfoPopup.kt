package com.mkrworld.waitingcallsms.ui.custom

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer
import android.view.Gravity
import android.graphics.PixelFormat
import android.view.LayoutInflater
import com.mkrworld.waitingcallsms.R


class WindowViewInfoPopup : WindowView {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".WindowViewInfoPopup"
    }

    protected val view: View
    protected val windowManagerParam: WindowManager.LayoutParams

    /**
     * Constructor
     *
     * @param context
     * @param windowManager
     */
    constructor(context: Context, windowManager: WindowManager) : super(context, windowManager) {
        Tracer.debug(TAG, "constructor: ")
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.activity_main, null)
        windowManagerParam = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
        windowManagerParam.horizontalMargin = 0F
        windowManagerParam.verticalMargin = 0F
        windowManagerParam.gravity = Gravity.BOTTOM or Gravity.LEFT
    }

    override fun getWindowView(): View {
        Tracer.debug(TAG, "getWindowView: ")
        return view
    }

    override fun getWindowManagerLayoutParam(): WindowManager.LayoutParams {
        Tracer.debug(TAG, "getWindowManagerLayoutParam: ")
        return windowManagerParam
    }
}