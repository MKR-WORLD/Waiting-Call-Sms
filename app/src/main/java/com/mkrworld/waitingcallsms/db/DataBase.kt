package com.mkrworld.waitingcallsms.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer

class DataBase : SQLiteOpenHelper {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".DataBase"
        const val DATABASE_NAME = "WaitingCallSms"
        private var instance: DataBase? = null
        private var context: Context? = null

        /**
         * Get the instance of this class
         *
         * @param context
         * @return
         */
        fun getInstance(context: Context): DataBase {
            this.context = context
            if (instance == null) {
                instance = DataBase(context)
            }
            return instance!!
        }
    }

    private val tableConatctInfo: TableContactInfo

    private constructor(context: Context) : super(context, DATABASE_NAME, null, 1) {
        DataBase.context = context
        tableConatctInfo = TableContactInfo.getInstance(context)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Tracer.debug(TAG, "onCreate()")
        tableConatctInfo.createTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Tracer.debug(TAG, "onUpgrade: ")
    }

    /**
     * Method to get the list of all Blocked Number
     *
     * @return List of the ContactInfo
     */
    internal fun getContactList(): ArrayList<TableContactInfo.ContactInfo> {
        Tracer.debug(TAG, "getContactList()")
        return tableConatctInfo?.getContactList(readableDatabase)
    }

    /**
     * Method to check weather the table contain the number in block list or not
     *
     * @param number
     * @return TRUE if contain, else FALSE
     */
    fun isContainNumber(number: String): Boolean {
        Tracer.debug(TAG, "isContainNumber()")
        return tableConatctInfo?.isContainNumber(readableDatabase, number)
    }

    /**
     * Method to save the Contact Info
     *
     * @param contactInfo
     */
    fun saveContactInfo(contactInfo: TableContactInfo.ContactInfo) {
        Tracer.debug(TAG, "saveContactInfo() ")
        tableConatctInfo?.saveContactInfo(writableDatabase, contactInfo)
    }

    /**
     * Method to remove the ContactInfo from Table
     *
     * @param contactInfo
     */
    fun removeContactInfo(contactInfo: TableContactInfo.ContactInfo) {
        Tracer.debug(TAG, "removeContactInfo: ")
        tableConatctInfo?.removeContactInfo(writableDatabase, contactInfo)
    }
}