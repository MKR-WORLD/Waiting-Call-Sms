package com.mkrworld.waitingcallsms.provider

import android.content.Context
import android.support.v4.app.Fragment
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.ui.fragment.FragmentBlockNumberList
import com.mkrworld.waitingcallsms.ui.fragment.FragmentFormBlockNumber
import com.mkrworld.waitingcallsms.ui.fragment.FragmentTemplate
import com.mkrworld.waitingcallsms.utils.Tracer

class FragmentProvider {

    enum class FragmentTag {
        NONE, FORM_BLOCK_NUMBER, BLOCK_NUMBER_LIST, TEMPLATE
    }

    companion object {

        private const val TAG: String = BuildConfig.BASE_TAG + ".FragmentProvider"

        /**
         * Method to get the Fragment
         * @param tag
         */
        fun getFragment(tag: String): Fragment {
            Tracer.debug(TAG, "getFragment: ")
            return when (tag) {
                FragmentTag.FORM_BLOCK_NUMBER.name -> {
                    FragmentFormBlockNumber()
                }
                FragmentTag.BLOCK_NUMBER_LIST.name -> {
                    FragmentBlockNumberList()
                }
                FragmentTag.TEMPLATE.name -> {
                    FragmentTemplate()
                }
                else -> {
                    Fragment()
                }
            }
        }

        /**
         * Method to get the Fragment
         * @param context
         * @param tag
         */
        fun getLabel(context: Context, tag: String): String {
            Tracer.debug(TAG, "getLabel: ")
            return when (tag) {
                FragmentTag.FORM_BLOCK_NUMBER.name -> {
                    "ADD CONTACT"
                }
                FragmentTag.BLOCK_NUMBER_LIST.name -> {
                    "CONTACT LIST"
                }
                FragmentTag.TEMPLATE.name -> {
                    "TEMPLATE"
                }
                else -> {
                    "MIS"
                }
            }
        }

    }
}