package edu.umsl.nasaviewer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

// this code was ripped and modified from https://www.tutorialkart.com/kotlin-android/android-sqlite-example-application/

class NASAAccessLogDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // upgrade policy is to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertNASALog(log: NASAAccessLogModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        val  entrynum = DatabaseUtils.queryNumEntries(db, DBContract.NASAEntry.TABLE_NAME) + 1
        values.put(DBContract.NASAEntry.COLUMN_ENTRYNUM, entrynum.toString())  // arbitrary number
        values.put(DBContract.NASAEntry.COLUMN_NASAID, log.nasaid)  // NASA assigned ID
        values.put(DBContract.NASAEntry.COLUMN_TIME, log.time)      // date and time

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.NASAEntry.TABLE_NAME, null, values)
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteNASALog(entrynum: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.NASAEntry.COLUMN_ENTRYNUM + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(entrynum)
        // Issue SQL statement.
        db.delete(DBContract.NASAEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readNASALog(entrynum: String): ArrayList<NASAAccessLogModel> {
        val nasa = ArrayList<NASAAccessLogModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.NASAEntry.TABLE_NAME + " WHERE " + DBContract.NASAEntry.COLUMN_ENTRYNUM + "='" + entrynum + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var nasaid: String
        var time: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                nasaid = cursor.getString(cursor.getColumnIndex(DBContract.NASAEntry.COLUMN_NASAID))
                time = cursor.getString(cursor.getColumnIndex(DBContract.NASAEntry.COLUMN_TIME))

                nasa.add(NASAAccessLogModel(entrynum, nasaid, time))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return nasa
    }

    fun readAllNASALog(): ArrayList<NASAAccessLogModel> {
        val users = ArrayList<NASAAccessLogModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.NASAEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var entrynum: String
        var nasaid: String
        var time: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                entrynum = cursor.getString(cursor.getColumnIndex(DBContract.NASAEntry.COLUMN_ENTRYNUM))
                nasaid = cursor.getString(cursor.getColumnIndex(DBContract.NASAEntry.COLUMN_NASAID))
                time = cursor.getString(cursor.getColumnIndex(DBContract.NASAEntry.COLUMN_TIME))

                users.add(NASAAccessLogModel(entrynum, nasaid, time))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return users
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NASAAccessLog.db"

        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.NASAEntry.TABLE_NAME + " (" +
                        DBContract.NASAEntry.COLUMN_ENTRYNUM + " TEXT PRIMARY KEY," +
                        DBContract.NASAEntry.COLUMN_NASAID + " TEXT," +
                        DBContract.NASAEntry.COLUMN_TIME + " TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.NASAEntry.TABLE_NAME
    }
}