package com.example.stepanenko.bookstore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stepanenko.bookstore.data.BookContract.BookEntry;

public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURRENT_BOOK_LOADER = 0;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final boolean OPERATION_SUCCESS = true;
    private static final boolean OPERATION_FAILED = false;
    private Uri bindCurrentBookUri;

    private EditText bindNameEditText;
    private EditText bindPriceEditText;
    private EditText bindQuantityEditText;
    private EditText bindSupplierEditText;
    private EditText bindSupplierPhoneEditText;

    private boolean dataHasChanged = false;

    private View.OnTouchListener currentTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            dataHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        bindCurrentBookUri = intent.getData();

        if (bindCurrentBookUri == null) {
            setTitle(getString(R.string.activity_title_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.activity_title_edit));
            getLoaderManager().initLoader(CURRENT_BOOK_LOADER, null, this);
        }

        bindNameEditText = findViewById(R.id.edit_book_name);
        bindPriceEditText = findViewById(R.id.edit_book_price);
        bindQuantityEditText = findViewById(R.id.edit_price_quantity);
        bindSupplierEditText = findViewById(R.id.edit_supplier);
        bindSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

        bindNameEditText.setOnTouchListener(currentTouchListener);
        bindPriceEditText.setOnTouchListener(currentTouchListener);
        bindQuantityEditText.setOnTouchListener(currentTouchListener);
        bindSupplierEditText.setOnTouchListener(currentTouchListener);
        bindSupplierPhoneEditText.setOnTouchListener(currentTouchListener);

        findViewById(R.id.decrease_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String actualQuantity = bindQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(actualQuantity)) {
                    actualQuantity = String.valueOf(0);
                }

                int integerQuantity = Integer.parseInt(actualQuantity);

                if (integerQuantity <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_invalid_quantity, Toast.LENGTH_SHORT).show();
                } else {
                    integerQuantity--;
                    bindQuantityEditText.setText(String.valueOf(integerQuantity));
                }
            }
        });

        findViewById(R.id.increase_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String actualQuantity = bindQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(actualQuantity)) {
                    actualQuantity = String.valueOf(0);
                }

                int qtyInt = Integer.parseInt(actualQuantity);

                qtyInt++;
                bindQuantityEditText.setText(String.valueOf(qtyInt));
            }
        });

        findViewById(R.id.make_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String callNumber = bindSupplierPhoneEditText.getText().toString().trim();
                if (!callNumber.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel",
                            callNumber, null));
                    if (ContextCompat.checkSelfPermission(EditActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        startActivity(intent);
                    }
                } else
                    Toast.makeText(EditActivity.this, R.string.toast_invalid_quantity, Toast.LENGTH_SHORT).show();
            }
        });

        Button saveItemButton = findViewById(R.id.edit_save_item);
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                finish();
            }
        });
    }

    private boolean saveData() {
        String nameString = "";
        float priceString;
        int quantityString;
        String supplierString = "";
        String supplierPhoneString = "";

        try {
            nameString = bindNameEditText.getText().toString().trim();
            priceString = Float.parseFloat(bindPriceEditText.getText().toString().trim());
            quantityString = Integer.parseInt(bindQuantityEditText.getText().toString().trim());
            supplierString = bindSupplierEditText.getText().toString().trim();
            supplierPhoneString = bindSupplierPhoneEditText.getText().toString().trim();
        } catch (NumberFormatException e) {
            return OPERATION_FAILED;
        }
        try {
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, getString(R.string.invalid_name),
                        Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_name));
            }
            if (priceString <= 0) {
                Toast.makeText(this, getString(R.string.invalid_price),
                        Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_price));
            }
            if (quantityString < 0) {
                Toast.makeText(this, getString(R.string.invalid_quantity),
                        Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_quantity));
            }
            if (TextUtils.isEmpty(supplierString)) {
                Toast.makeText(this, getString(R.string.invalid_supplier),
                        Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_supplier));
            }
            if (TextUtils.isEmpty(supplierPhoneString)) {
                Toast.makeText(this, getString(R.string.invalid_supplier_phone),
                        Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_supplier_phone));
            }
        } catch (IllegalArgumentException e) {
            return OPERATION_FAILED;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, nameString);
        values.put(BookEntry.COLUMN_PRICE, priceString);
        values.put(BookEntry.COLUMN_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        if (bindCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.toast_added_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_added_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(bindCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.toast_edited_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_edited_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return OPERATION_SUCCESS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (bindCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.edit_delete_item);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_delete_item:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!dataHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!dataHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
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
                bindCurrentBookUri,
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

            bindNameEditText.setText(name);
            bindPriceEditText.setText(Integer.toString(price));
            bindQuantityEditText.setText(Integer.toString(quantity));
            bindSupplierEditText.setText(supplier);
            bindSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bindNameEditText.setText("");
        bindPriceEditText.setText("");
        bindQuantityEditText.setText("");
        bindSupplierEditText.setText("");
        bindSupplierPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        if (bindCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(bindCurrentBookUri, null, null);
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