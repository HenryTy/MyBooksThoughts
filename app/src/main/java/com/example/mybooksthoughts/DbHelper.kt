package com.example.mybooksthoughts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class DbHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${Columns.name} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Columns.book_name} TEXT, " +
                "${Columns.note} INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun saveNotes(name: String, text: String){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(Columns.book_name, name)
            put(Columns.note, text)
        }
        db?.insert(Columns.name, null, values)
    }

    fun getNotes(name: String): MutableList<String>{
        val result: MutableList<String> = ArrayList()
        val db = readableDatabase
        val cursor = db.rawQuery("Select * from " + Columns.name + " where " + Columns.book_name + " = " + name , null)
        with(cursor){
            while(moveToNext()){
                result.add(getString(getColumnIndex(Columns.note)))
            }
        }
        return result
    }

    companion object {
        const val DB_NAME = "notes"
        const val DB_VERSION = 1;
    }

    object Columns : BaseColumns {
        const val name = "notes"
        const val book_name = "book_name"
        const val note = "note"
    }
}