package com.example.stepanenko.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.stepanenko.bookstore.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "books.db";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BookEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, " + BookEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, " + BookEntry.COLUMN_SUPPLIER + " TEXT NOT NULL," + BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}