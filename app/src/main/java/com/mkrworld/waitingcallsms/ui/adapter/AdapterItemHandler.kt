package com.mkrworld.waitingcallsms.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mkrworld.androidlib.ui.adapter.BaseAdapterItemHandler
import com.mkrworld.androidlib.ui.adapter.BaseViewHolder

/**
 * Created by mkr on 14/3/18.
 */

class AdapterItemHandler : BaseAdapterItemHandler() {
    /**
     * Type of view hold by adapter
     */
    enum class AdapterItemViewType {
        NONE
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (getItemViewType(viewType)) {
            else -> return object : BaseViewHolder<Any>(FrameLayout(inflater.context)) {
                protected override fun bindData(o: Any) {

                }
            }
        }
    }

    /**
     * Method to get the ENUM as per viewType
     *
     * @return
     */
    private fun getItemViewType(viewType: Int): AdapterItemViewType {
        return AdapterItemViewType.values()[viewType]
    }
}
