package com.nepalicoders.contentproviderclientapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCountry, etContinent, etWhereToUpdate, etNewContinent, etWhereToDelete, etQueryRowById;
    private Button btnInsert, btnUpdate, btnDelete, btnQueryRowById, btnDisplayAll;

    private static final String TAG = MainActivity.class.getSimpleName();

    // COLUMNS
    private static final String _ID = BaseColumns._ID;
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_CONTINENT = "continent";

    private static final Uri CONTENT_URI = Uri.parse("content://com.nepalicoders.contentproviderdemo.data.NationProvider/countries");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCountry = (EditText) findViewById(R.id.etCountry);
        etContinent = (EditText) findViewById(R.id.etContinent);
        etWhereToUpdate = (EditText) findViewById(R.id.etWhereToUpdate);
        etNewContinent = (EditText) findViewById(R.id.etUpdateContinent);
        etQueryRowById = (EditText) findViewById(R.id.etQueryByRowId);
        etWhereToDelete = (EditText) findViewById(R.id.etWhereToDelete);

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnQueryRowById = (Button) findViewById(R.id.btnQueryByID);
        btnDisplayAll = (Button) findViewById(R.id.btnDisplayAll);

        btnInsert.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnQueryRowById.setOnClickListener(this);
        btnDisplayAll.setOnClickListener(this);

//        mNationDbHelper = new NationDbHelper(this);
//        mDatabase = mNationDbHelper.getWritableDatabase(); // READ/WRITE
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnInsert:
                insert();
                break;

            case R.id.btnUpdate:
                update();
                break;

            case R.id.btnDelete:
                delete();
                break;

            case R.id.btnQueryByID:
                queryRowById();
                break;

            case R.id.btnDisplayAll:
                queryAndDisplayAll();
                break;
        }
    }

    private void insert() {

        String countryName = etCountry.getText().toString();
        String continentName = etContinent.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COUNTRY, countryName);
        contentValues.put(COLUMN_CONTINENT, continentName);

        Uri uri = CONTENT_URI;
        Uri uriRowInserted = getContentResolver().insert(uri, contentValues);
        Log.i(TAG, "Item inserted at: " + uriRowInserted);

    }

    private void update() {

        String whereCountry = etWhereToUpdate.getText().toString();
        String newContinet = etNewContinent.getText().toString();

        String selection = COLUMN_COUNTRY + " = ?";
        String[] selectionArgs = {whereCountry}; // e.g. WHERE country = ? = Japan

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTINENT, newContinet);

        Uri uri = CONTENT_URI;
        Log.i(TAG, "" + uri);
        int rowsUpdated = getContentResolver().update(uri, contentValues, selection, selectionArgs);
        Log.i(TAG, "Number of rows updated: " + rowsUpdated);

    }

    private void delete() {

        String countryName = etWhereToDelete.getText().toString();

        String selection = COLUMN_COUNTRY + " = ?";
        String[] selectionArgs = {countryName}; // WHERE country = "Japan"

        Uri uri = Uri.withAppendedPath(CONTENT_URI, countryName);
        Log.i(TAG, "" + uri);
        int rowsDeleted = getContentResolver().delete(uri, selection, selectionArgs);
        Log.i(TAG, "Number of rows deleted: " + rowsDeleted);

    }

    private void queryRowById() {

        String rowId = etQueryRowById.getText().toString();

        String[] projection = {
                _ID,
                COLUMN_COUNTRY,
                COLUMN_CONTINENT
        };

        // Filter results. Make these null if you want to query all rows
        String selection = _ID + " = ?"; // _id = ?
        String[] selectionArgs = {rowId}; // Replace '?' by rowId in runtime e.g. _id = 5
        String sortOrder = null; // Ascending or Descending order. null means default

        Uri uri = Uri.withAppendedPath(CONTENT_URI, rowId);
        Log.i(TAG, "" + uri);
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToNext()) {
            String str = "";
            String[] columns = cursor.getColumnNames();
            for (String column : columns) {
                str += "\t" + cursor.getString(cursor.getColumnIndex(column));
            }
            str += "\n";

            cursor.close();
            Log.i(TAG, str);
        }

    }

    private void queryAndDisplayAll() {

        String[] projection = {
                _ID,
                COLUMN_COUNTRY,
                COLUMN_CONTINENT
        };

        // Filter results. Make these null if you want to query all rows
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = COLUMN_COUNTRY + " ASC"; // Ascending or Descending order. null means default

        Uri uri = CONTENT_URI;
        Log.i(TAG, "" + uri);
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            String str = "";
            while (cursor.moveToNext()) {
                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    str += "\t" + cursor.getString(cursor.getColumnIndex(column));
                }
                str += "\n";
            }

            cursor.close();
            Log.i(TAG, str);
        }

    }

    @Override
    protected void onDestroy() {
//        mDatabase.close();
        super.onDestroy();
    }
}
