package com.example.stepanenko.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.stepanenko.bookstore.data.BookContract.BookEntry;
import com.example.stepanenko.bookstore.data.BookDbHelper;

public class CatalogActivity extends AppCompatActivity {
    private BookDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        dbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {BookEntry._ID, BookEntry.COLUMN_NAME, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_QUANTITY, BookEntry.COLUMN_SUPPLIER, BookEntry.COLUMN_SUPPLIER_PHONE};

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);
        TextView displayView = findViewById(R.id.text_view_books);

        //simple check, if everything is OK
        try {
            displayView.setText("Table rows: " + cursor.getCount() + "\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_NAME + " - " + BookEntry.COLUMN_PRICE + " - " + BookEntry.COLUMN_QUANTITY + " - " + BookEntry.COLUMN_SUPPLIER + " - " + BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " + currentPrice + " - " + currentQuantity + " - " + currentSupplier + " - " + currentPhone));
            }
        } finally {
            cursor.close();
        }
    }

    private void addBook() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testDataValues = new ContentValues();
        testDataValues.put(BookEntry.COLUMN_NAME, "The Lord Of The Rings");
        testDataValues.put(BookEntry.COLUMN_PRICE, 12);
        testDataValues.put(BookEntry.COLUMN_QUANTITY, 124);
        testDataValues.put(BookEntry.COLUMN_SUPPLIER, "New Line Production");
        testDataValues.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+44 3069 990274");
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, testDataValues);
        Log.v("ROW_ID", "Id is: " + newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_test_data:
                addBook();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
