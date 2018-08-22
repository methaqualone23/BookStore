package com.example.stepanenko.bookstore.data;

import android.provider.BaseColumns;

public final class BookContract {
    public static abstract class BookEntry implements BaseColumns {
      public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
    }
}
