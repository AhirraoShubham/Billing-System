package com.example.invoicebilling.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.invoicebilling.pojo.Product;

import java.util.ArrayList;
import java.util.Arrays;


public class SqliteDatabase extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "invoice.db";
    private Context mContext;

    //Table Name
    private static final String TABLE_PRODUCT_NAME = "tbl_product_details";
    private static final String TABLE_PRODUCT_BILL = "tbl_product_bill";
    private static final String TABLE_PRODUCT_CATEGORY = "tbl_product_category";
    private static final String TABLE_PRODUCT_REPORT = "tbl_product_report";

   //Staff Details Column
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "prod_name";
    private static final String COLUMN_PRICE = "prod_price";
    private static final String COLUMN_IMAGE = "prod_image";
    private static final String COLUMN_CATEGORY = "prod_category";
    private static final String COLUMN_AMOUNT = "prod_amount";

    //product bill column
    private static final String COLUMN_QTY = "prod_qty";

    Product product;



    private static SqliteDatabase sqliteDatabase = null;

    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SqliteDatabase getInstance(Context context) {

        if (sqliteDatabase == null) {
            sqliteDatabase = new SqliteDatabase(context);
        }

        return sqliteDatabase;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCT_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME +
                " TEXT," + COLUMN_PRICE + " TEXT,"+ COLUMN_CATEGORY + " TEXT,"
                +COLUMN_IMAGE + " BLOB" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_PRODUCTS_BILL_TABLE = "CREATE TABLE " + TABLE_PRODUCT_BILL +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME +
                " TEXT," +COLUMN_QTY + " TEXT,"
                +COLUMN_PRICE + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_BILL_TABLE);

        String CREATE_PRODUCTS_CATEGORY = "CREATE TABLE " + TABLE_PRODUCT_CATEGORY +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COLUMN_CATEGORY + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_CATEGORY);

        String CREATE_PRODUCTS_REPORT = "CREATE TABLE " + TABLE_PRODUCT_REPORT +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COLUMN_NAME + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_REPORT);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_BILL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_REPORT);

        onCreate(db);
    }

    // Product List
    public Cursor getProoducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_PRODUCT_NAME, null, null, null, null, null, null);

    }

    // Product bill List
    public Cursor getProoductBill() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_PRODUCT_BILL, null, null, null, null, null, null);

    }

    // add product
    public void addProduct(Product product){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, product.getProdName());
        values.put(COLUMN_PRICE,product.getProdPrice());
        values.put(COLUMN_CATEGORY,product.getProdCategory());
        values.put(COLUMN_IMAGE, product.getProdImage());

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("tag", " insertData:  " + " ID: " + product.getProdId() + " name: " + product.getProdName()
                +" price "+product.getProdPrice() +" category "+product.getProdCategory()
                +" image: "+ Arrays.toString(product.getProdImage()));

        db.insert(TABLE_PRODUCT_NAME, null, values);
    }

    // delete product
    public void deleteProductBill(int ID){
        SQLiteDatabase mDb = this.getWritableDatabase();
        int count = mDb.delete(TABLE_PRODUCT_BILL,
                "ID = ?",
                new String[]{String.valueOf(ID)}
        );

        Log.e("tag", " deleted prodct " + ID);

        if (count >= 0) {
        } else {
        }
    }

    // update services
    public void updateProduct(int ID, String name, String price, byte[] image ) {

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);

        SQLiteDatabase mDb = this.getWritableDatabase();

        Log.e("tag", "updated services:  " + "ID " + ID + " " + name + " price" + price + " image" + image
        );

        int count =
                mDb.update(TABLE_PRODUCT_NAME,
                        values,
                        "ID = ?",
                        new String[]{String.valueOf(ID)}
                );

        if (count >= 0) {
        } else {
        }

    }

    // add product to bill
    public void addProductBill(Product product){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, product.getProdName());
        values.put(COLUMN_PRICE,product.getProdPrice());
        values.put(COLUMN_QTY, product.getProdQty());

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("tag", " insertData:  " + " ID: " + product.getProdId() + " name: " + product.getProdName()
                +" price"+product.getProdPrice()
                +" qty: "+ product.getProdQty());

        db.insert(TABLE_PRODUCT_BILL, null, values);
    }

    // update product bill qty
    public void updateProductQty(int ID,String qty ) {

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put(COLUMN_QTY, qty);

        SQLiteDatabase mDb = this.getWritableDatabase();

        Log.e("tag", "updated services:  " + "ID " + ID + " Qty: " + qty);

        int count =
                mDb.update(TABLE_PRODUCT_BILL,
                        values,
                        "ID = ?",
                        new String[]{String.valueOf(ID)}
                );

        if (count >= 0) {
        } else {
        }

    }

    // add product category
    public void addProductCategory(Product product){
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY, product.getProdCategory());

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("tag", " insertData:  "
                +" product category: "+ product.getProdCategory());
        db.insert(TABLE_PRODUCT_CATEGORY, null, values);
    }

    // Product category List
    public Cursor getProodCategory() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_PRODUCT_CATEGORY, null, null, null, null, null, null);

    }

    //getting all product categorywise
    public Cursor getProdCate(String prod_cate) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM tbl_product_details WHERE prod_category= '"+prod_cate+"' ", null);

    }

    //add to report
    public void addProductReport(String product){
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, product);

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("tag", " Report Data:  " + " ID: " + product + " name: "
        );

        db.insert(TABLE_PRODUCT_REPORT, null, values);
    }

    // Checking Email is already exists or not.
    public void CheckingCategoryAlreadyExistsOrNot(String category ){

        // Opening SQLite database write permission.
        SQLiteDatabase db = this.getWritableDatabase();

        // Adding search email query to cursor.
        Cursor cursor = db.query(TABLE_PRODUCT_CATEGORY, null, " " +
                        COLUMN_CATEGORY + "=?",
                new String[]{category}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {

                cursor.moveToFirst();

                // If Email is already exists then Result variable value set as Email Found.
              /*  Toast.makeText(mContext,
                        category+"  already exists:  ", Toast.LENGTH_SHORT).show();*/

                Log.e("tag", " already exists:  "+category);


                // Closing cursor.
                cursor.close();
            }
        }
        // Calling method to check final result and insert data into SQLite database.

        addProductCategory(new Product(category));


    }



}
