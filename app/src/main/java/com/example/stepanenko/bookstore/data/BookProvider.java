package com.example.stepanenko.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.stepanenko.bookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    private static final UriMatcher currentUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        currentUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        currentUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    private BookDbHelper currentDbHelper;

    @Override
    public boolean onCreate() {
        currentDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = currentDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = currentUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = currentUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String name = values.getAsString(BookContract.BookEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires title");
        }

        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Book requires valid price");
        }
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }

        String supplier = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Book requires supplier");
        }

        SQLiteDatabase database = currentDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = currentUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookEntry.COLUMN_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires title");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Book requires supplier");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = currentDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = currentDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = currentUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = currentUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
