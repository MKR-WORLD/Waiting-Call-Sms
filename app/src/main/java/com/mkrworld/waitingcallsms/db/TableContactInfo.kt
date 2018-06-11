package com.mkrworld.waitingcallsms.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.mkrworld.waitingcallsms.BuildConfig
import com.mkrworld.waitingcallsms.utils.Tracer

class TableContactInfo {
    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".TableContactInfo"
        private var instance: TableContactInfo? = null
        private var context: Context? = null
        private val TABLE_NAME = "Table_Contact_Info"
        private val COL_ID: String = "id"
        private val COL_COUNTRY_CODE: String = "country_code"
        private val COL_NUMBER: String = "number"
        private val COL_NAME: String = "name"

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
    internal fun getContactInfo(database: SQLiteDatabase, countryCode: String, number: String): ContactInfo? {
        Tracer.debug(TAG, "getContactInfo()")
        try {
            val conditionSelect1 = "($COL_COUNTRY_CODE=\"${countryCode.trim()}\")"
            val conditionSelect2 = "($COL_NUMBER=\"${number.trim()}\")"
            val query = "Select * from $TABLE_NAME where $conditionSelect1 AND $conditionSelect2;"
            val cursor: Cursor? = database.rawQuery(query, null)
            if (cursor != null) {
                return if (cursor.moveToFirst()) {
                    getContactInfo(cursor, true)
                } else {
                    cursor.close()
                    return null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Tracer.error(TAG, "getContactInfo() " + e.message)
        }
        return null
    }

    /**
     * Method to check weather the table contain the number in block list or not
     *
     * @param database
     * @param number
     * @return TRUE if contain, else FALSE
     */
    internal fun isContainNumber(database: SQLiteDatabase, countryCode: String, number: String): Boolean {
        Tracer.debug(TAG, "isContainNumber()")
        return getContactInfo(database, countryCode, number) != null

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
            dto.id = cursor?.getLong(cursor.getColumnIndex(COL_ID))
            dto.countryCode = cursor?.getString(cursor.getColumnIndex(COL_COUNTRY_CODE))
            dto.number = cursor?.getString(cursor.getColumnIndex(COL_NUMBER))
            dto.name = cursor?.getString(cursor.getColumnIndex(COL_NAME))
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
            val contactInfoOld = getContactInfo(database, contactInfo.countryCode, contactInfo.number)
            if (contactInfoOld != null) {
                contentValues.put(COL_ID, contactInfoOld.id)
            }
            contentValues.put(COL_COUNTRY_CODE, contactInfo.countryCode)
            contentValues.put(COL_NUMBER, contactInfo.number)
            contentValues.put(COL_NAME, contactInfo.name)
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
            database.delete(TABLE_NAME, "$COL_COUNTRY_CODE = ? AND $COL_NUMBER = ?", arrayOf(contactInfo.countryCode, contactInfo.number))
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
            query += "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            query += "$COL_COUNTRY_CODE VARCHAR NOT NULL, "
            query += "$COL_NUMBER VARCHAR NOT NULL, "
            query += "$COL_NAME VARCHAR NOT NULL"
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

        /**
         * Contact DB Row ID
         */
        var id: Long = -1L
            get() {
                return field ?: -1
            }
            set(value) {
                field = if (value == null) {
                    -1
                } else {
                    value
                }
            }

        /**
         * Contact Country Code To be blocked, Ex : INDIA - +91
         */
        var countryCode: String = ""
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
         * Contact Number To be blocked, Ex : INDIA - XXXXXXXXXX
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