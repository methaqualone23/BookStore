package com.example.stepanenko.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stepanenko.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;
    BookCursorAdapter bookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        ListView inStockListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        inStockListView.setEmptyView(emptyView);
        bookCursorAdapter = new BookCursorAdapter(this, null);
        inStockListView.setAdapter(bookCursorAdapter);
        inStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(CatalogActivity.this, EditActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                detailsIntent.setData(currentBookUri);
                startActivity(detailsIntent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertTestData() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, "Lord of The Rings");
        values.put(BookEntry.COLUMN_PRICE, 12);
        values.put(BookEntry.COLUMN_QUANTITY, 124);
        values.put(BookEntry.COLUMN_SUPPLIER, "New Wave Publisher");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "44 7874 85 70");
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values); //helps to update the list
    }

    private void deleteAllData() {
        getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_test_data:
                insertTestData();
                return true;
            case R.id.delete_all_data:
                deleteAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookCursorAdapter.swapCursor(null);
    }

    public void reduceQuantity(long id, int inStockQuantity) {
        if (inStockQuantity >= 1) {
            inStockQuantity--;
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, inStockQuantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, R.string.toast_edited_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_edited_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
