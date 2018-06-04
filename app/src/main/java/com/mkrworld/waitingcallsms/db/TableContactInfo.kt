package com.mkrworld.waitingcallsms.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer

class TableContactInfo {
    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".TableContactInfo"
        private var instance: TableContactInfo? = null
        private var context: Context? = null
        private val TABLE_NAME = "Table_Contact_Info"
        private val COLLUMN_CONTACT_NUMBER: String = "contact_number"
        private val COLLUMN_CONTACT_NAME: String = "contact_name"

        /**
         * Method to get the instance.
         *
         * @param context
         * @return Current object of this class.
         */
        fun getInstance(context: Context): TableContactInfo {
            Tracer.debug(TAG, "getInstance() ")
            this.context = context
            if (instance == null) {
                instance = TableContactInfo()
            }
            return instance!!
        }
    }

    /**
     * Method to get the list of all Blocked Number
     *
     * @param database
     * @return List of the ContactInfo
     */
    internal fun getContactList(database: SQLiteDatabase): ArrayList<ContactInfo> {
        Tracer.debug(TAG, "getContactList()")
        var itemList: ArrayList<ContactInfo> = ArrayList<ContactInfo>()
        try {
            val query = "Select * from $TABLE_NAME;"
            val cursor: Cursor? = database.rawQuery(query, null)
            if (cursor != null) {
                while (cursor?.moveToNext()) {
                    val dto = getContactInfo(cursor, false)
                    if (dto != null) {
                        itemList.add(dto)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "getContactList() " + e.message)
        }
        return itemList
    }

    /**
     * Method to check weather the table contain the number in block list or not
     *
     * @param database
     * @param number
     * @return TRUE if contain, else FALSE
     */
    internal fun isContainNumber(database: SQLiteDatabase, number: String): Boolean {
        Tracer.debug(TAG, "isContainNumber()")
        try {
            val conditionSelect1 = "($COLLUMN_CONTACT_NUMBER=\"${number.trim()}\")"
            val query = "Select * from $TABLE_NAME where $conditionSelect1;"
            val cursor: Cursor? = database.rawQuery(query, null)
            if (cursor != null) {
                return if (cursor.count > 0) {
                    cursor.close()
                    true
                } else {
                    cursor.close()
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "isContainNumber() " + e.message)
        }
        return false
    }


    /**
     * Method to get the ContactInfo from cursor
     *
     * @param cursor
     * @param isCloseCursor
     * @return
     */
    private fun getContactInfo(cursor: Cursor, isCloseCursor: Boolean): ContactInfo? {
        Tracer.debug(TAG, "getContactInfo: ")
        return try {
            val dto = ContactInfo()
            dto.number = cursor?.getString(cursor.getColumnIndex(COLLUMN_CONTACT_NUMBER))
            dto.name = cursor?.getString(cursor.getColumnIndex(COLLUMN_CONTACT_NAME))
            dto
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "getContactInfo() " + e.message)
            null
        } finally {
            if (isCloseCursor) {
                cursor?.close()
            }
        }
    }

    /**
     * Method to save the Contact Info
     *
     * @param database
     * @param contactInfo
     */
    internal fun saveContactInfo(database: SQLiteDatabase, contactInfo: ContactInfo) {
        Tracer.debug(TAG, "saveContactInfo() ")
        try {
            val contentValues = ContentValues()
            contentValues.put(COLLUMN_CONTACT_NUMBER, contactInfo.number)
            contentValues.put(COLLUMN_CONTACT_NAME, contactInfo.name)
            database.replace(TABLE_NAME, null, contentValues)
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "saveContactInfo() " + e.message)
        }
    }

    /**
     * Method to remove the ContactInfo from Table
     *
     * @param database
     * @param contactInfo
     */
    internal fun removeContactInfo(database: SQLiteDatabase, contactInfo: ContactInfo) {
        Tracer.debug(TAG, "removeContactInfo: ")
        try {
            database.delete(TABLE_NAME, "$COLLUMN_CONTACT_NUMBER = ?", arrayOf(contactInfo.number))
        } catch (e: Exception) {
            Tracer.error(TAG, "removeGestureData: " + e.message);
        }
    }

    /**
     * Method to create the table which hold the contact number
     *
     * @param db
     */
    internal fun createTable(db: SQLiteDatabase) {
        Tracer.debug(TAG, "createTable() ")
        try {
            var query = "Create table $TABLE_NAME("
            query += "$COLLUMN_CONTACT_NUMBER INTEGER PRIMARY KEY AUTOINCREMENT, "
            query += "$COLLUMN_CONTACT_NAME VARCHAR UNIQUE NOT NULL"
            query += ")"
            Tracer.error(TAG, "createTable(QUERY) $query")
            db.execSQL(query)
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "createTable() " + e.message)
        }

    }

    /**
     * Class to hold the Info of the Contact
     */
    class ContactInfo {

        companion object {
            /**
             * Method to get the Number in the International Format
             * @param nationalNumber
             * @param countryCode
             */
            fun getContactNumber(nationalNumber: String, countryCode: String): String {
                return "+$countryCode$nationalNumber"
            }
        }

        /**
         * Contact Number To be blocked, Ex : INDIA - +91XXXXXXXX
         */
        var number: String = ""
            get() {
                return field.trim()
            }
            set(value) {
                field = if (value == null) {
                    ""
                } else {
                    value.trim()
                }
            }

        /**
         * Contact Name To be blocked
         */
        var name: String = ""
            get() {
                return field.trim()
            }
            set(value) {
                field = if (value == null) {
                    ""
                } else {
                    value.trim()
                }
            }
    }

}