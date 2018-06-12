package com.mkrworld.waitingcallsms.dto

import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.provider.FragmentProvider
import com.mkrworld.waitingcallsms.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 * Class to hold the mData of the Merchant Home Tab
 */
class DTOMKRTab {

    companion object {
        private val TAG = BuildConfig.BASE_TAG + ".DTOMKRTab"
    }

    /**
     * Method to get the label of the code
     *
     * @return
     */
    var label: String = ""
        get() {
            return field?.trim()
        }

    /**
     * Method to get the RowType
     *
     * @return
     */
    var tabType: FragmentProvider.FragmentTag = FragmentProvider.FragmentTag.NONE

    /**
     * Method to get the RowType
     *
     * @return
     */
    var isFocused: Boolean = false

    /**
     * Constructor
     *
     * @param tabType
     * @param label
     */
    constructor(tabType: FragmentProvider.FragmentTag, label: String) {
        Tracer.debug(TAG, "Constructor: $tabType  $label")
        this.tabType = tabType
        this.label = label
    }


    override fun equals(obj: Any?): Boolean {
        if (obj is DTOMKRTab) {
            val dto = obj as DTOMKRTab?
            if (dto!!.tabType == tabType && dto.label!!.equals(label!!, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return tabType!!.hashCode()
    }
}
