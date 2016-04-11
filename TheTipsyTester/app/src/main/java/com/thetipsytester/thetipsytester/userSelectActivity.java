package com.thetipsytester.thetipsytester;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class userSelectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        mAdapter = new SimpleCursorAdapter(this, R.layout.user_list_item, null,
                new String[]{"name","gender", "weight"},
                new int[] {R.id.txtTitle, R.id.txtGender, R.id.txtWeight}, 0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                return false;
            }
        });



        ListView listView = (ListView) findViewById(R.id.lstUsers);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void addUser(View view){
        //When the + button is clicked from user selection page
        Intent intent = new Intent(this, newUserActivity.class);

        startActivity(intent);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader with the following query parameters.
        String where = null;

        return new CursorLoader(this, UserContentProvider.CONTENT_URI,
                new String[]{"_id", "name", "gender", "weight"}, where, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (getIntent().getStringExtra("activity") != null && getIntent().getStringExtra("activity").equals("BAC")) {

            Intent intent = new Intent(this, bacCalculatorActivity.class);
            intent.putExtra("rowid", id);
            intent.putExtra("rowid", id);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("rowid", id);
            startActivity(intent);
        }


    }
}
