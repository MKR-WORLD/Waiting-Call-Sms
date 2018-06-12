package com.mkrworld.waitingcallsms.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.mkrworld.androidlib.callback.OnBaseFragmentListener
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.dto.DTOMKRTab
import com.mkrworld.waitingcallsms.provider.FragmentProvider
import com.mkrworld.waitingcallsms.utils.Tracer

/**
 * Created by mkr on 10/5/18.
 */

class MKRTabAdapter : FragmentStatePagerAdapter, ViewPager.OnPageChangeListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MKRTabAdapter"
    }

    private var mHomeTabList: ArrayList<DTOMKRTab>
    private var fragmentManager: FragmentManager

    /**
     * Constructor
     * @param fragmentManager
     * @param homeTabList
     */
    constructor(fragmentManager: FragmentManager, homeTabList: ArrayList<DTOMKRTab>) : super(fragmentManager) {
        this.fragmentManager = fragmentManager
        mHomeTabList = ArrayList<DTOMKRTab>()
        if (homeTabList != null) {
            mHomeTabList.addAll(homeTabList)
        }
    }

    override fun getItem(position: Int): Fragment? {
        return FragmentProvider.getFragment(mHomeTabList.get(position).tabType.name)
    }

    override fun getCount(): Int {
        return mHomeTabList?.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mHomeTabList[position].label
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        Tracer.debug(TAG, "onPageSelected: $position")
        val fragment = fragmentManager.findFragmentByTag(mHomeTabList[position].label)
        if (fragment != null && fragment is OnBaseFragmentListener) {
            (fragment as OnBaseFragmentListener).onPopFromBackStack()
        }
    }
}