package com.nepalicoders.contentproviderdemo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.nepalicoders.contentproviderdemo.data.NationContract.CONTENT_AUTHORITY;
import static com.nepalicoders.contentproviderdemo.data.NationContract.PATH_COUNTRIES;

/**
 * Created by Sulav on 8/11/17.
 */

public class NationProvider extends ContentProvider {

    private static final String TAG = NationProvider.class.getSimpleName();

    private NationDbHelper databaseHelper;

    // constants for the operation
    private static final int COUNTRIES = 1;                 // For whole table
    private static final int COUNTRIES_COUNTRY_NAME = 2;    // For a specific row in a table identified by COUNTRY_NAME
    private static final int COUNTRIES_ID = 3;              // For a specific row in a table identified by _ID

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES, COUNTRIES); // content://com.nepalicoders.contentproviderdemo.data.NationProvider/countries
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES + "/#", COUNTRIES_ID); // content://com.nepalicoders.contentproviderdemo.data.NationProvider/countries/#
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES + "/*", COUNTRIES_COUNTRY_NAME); // content://com.nepalicoders.contentproviderdemo.data.NationProvider/countries/*
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new NationDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {

            case COUNTRIES:
                cursor = database.query(NationContract.NationEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;

            case COUNTRIES_ID:
                selection = NationContract.NationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(NationContract.NationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException(TAG + " Query unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        switch (uriMatcher.match(uri)) {

            case COUNTRIES:
                return insertRecord(uri, values, NationContract.NationEntry.TABLE_NAME);

            default:
                throw new IllegalArgumentException(TAG + " Insert unknown URI: " + uri);
        }

    }

    private Uri insertRecord(Uri uri, ContentValues values, String tableName) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long rowId = database.insert(tableName, null, values);

        if (rowId == -1) {
            Log.e(TAG, "Insert error for URI " + uri);
            return null;
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, rowId);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {

            case COUNTRIES: // To delete whole table
                return deleteRecord(uri, null, null, NationContract.NationEntry.TABLE_NAME);

            case COUNTRIES_ID: // To delete a row by _ID
                selection = NationContract.NationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteRecord(uri, selection, selectionArgs, NationContract.NationEntry.TABLE_NAME);

            case COUNTRIES_COUNTRY_NAME: // To delete a row by COUNTRY NAME
                selection = NationContract.NationEntry.COLUMN_COUNTRY + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return deleteRecord(uri, selection, selectionArgs, NationContract.NationEntry.TABLE_NAME);

            default:
                throw new IllegalArgumentException(TAG + " Delete unknown URI: " + uri);

        }

    }

    private int deleteRecord(Uri uri, String selection, String[] selectionArgs, String tableName) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int rowsDeleted = database.delete(tableName, selection, selectionArgs);

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {

            case COUNTRIES:
                return updateRecord(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException(TAG + " Update unknown URI: " + uri);

        }
    }

    private int updateRecord(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int rowsUpdated = database.update(NationContract.NationEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }
}
