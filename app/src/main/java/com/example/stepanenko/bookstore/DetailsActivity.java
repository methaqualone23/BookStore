package com.example.stepanenko.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepanenko.bookstore.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_BOOK_LOADER = 0;

    private Uri detailsBookUri;

    private TextView detailsNameText;
    private TextView detailsPriceText;
    private EditText detailsQuantityText;
    private TextView detailsSupplierText;
    private TextView detailsSupplierPhoneText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        detailsBookUri = intent.getData();

        detailsNameText = findViewById(R.id.details_name);
        detailsPriceText = findViewById(R.id.details_price);
        detailsQuantityText = findViewById(R.id.details_edit_quantity);
        detailsSupplierText = findViewById(R.id.details_supplier);
        detailsSupplierPhoneText = findViewById(R.id.details_supplier__phone);

        final Button deleteItem = findViewById(R.id.details_delete_item);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
                deleteItem();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
       if (detailsBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.details_edit_item);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
            case R.id.details_edit_item:
                Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
/*                long id;
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                editIntent.setData(currentBookUri);*/
                startActivity(editIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                detailsBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            detailsNameText.setText(name);
            detailsPriceText.setText(price);
            detailsPriceText.setText(Integer.toString(price));
            detailsQuantityText.setText(Integer.toString(quantity));
            detailsSupplierText.setText(supplier);
            detailsSupplierPhoneText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailsNameText.setText("");
        detailsPriceText.setText("");
        detailsQuantityText.setText("");
        detailsSupplierText.setText("");
        detailsSupplierPhoneText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (detailsBookUri != null) {
            int rowsDeleted = getContentResolver().delete(detailsBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.toast_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}