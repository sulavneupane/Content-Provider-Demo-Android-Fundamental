package com.nepalicoders.contentproviderdemo;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.nepalicoders.contentproviderdemo.data.NationContract;

public class NationsListActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter simpleCursorAdapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getLoaderManager().initLoader(10, null, this);

        String[] from = {NationContract.NationEntry.COLUMN_COUNTRY, NationContract.NationEntry.COLUMN_CONTINENT};
        int[] to = {R.id.txvCountryName, R.id.txvContinentName};

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.nation_list_item, null, from, to, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor = (Cursor) parent.getItemAtPosition(position);

                int _id = cursor.getInt(cursor.getColumnIndex(NationContract.NationEntry._ID));
                String country = cursor.getString(cursor.getColumnIndex(NationContract.NationEntry.COLUMN_COUNTRY));
                String continent = cursor.getString(cursor.getColumnIndex(NationContract.NationEntry.COLUMN_CONTINENT));

                Intent intent = new Intent(NationsListActivity.this, NationsEditActivity.class);
                intent.putExtra("_id", _id);
                intent.putExtra("country", country);
                intent.putExtra("continent", continent);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NationsListActivity.this, NationsEditActivity.class);
                intent.putExtra("_id", 0);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                NationContract.NationEntry._ID,
                NationContract.NationEntry.COLUMN_COUNTRY,
                NationContract.NationEntry.COLUMN_CONTINENT
        };

        // Returns a CursorLoader object that carries a Cursor Object.
        // The cursor object contains all rows queried from database using ContentProvider.
        return new CursorLoader(this, NationContract.NationEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }
}
