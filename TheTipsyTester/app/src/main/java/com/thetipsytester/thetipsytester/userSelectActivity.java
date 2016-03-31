package com.thetipsytester.thetipsytester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class userSelectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        mAdapter = new SimpleCursorAdapter(this, R.layout.user_list_item, null,
                new String[]{"name","gender"},
                new int[] {R.id.txtTitle, R.id.txtGender}, 0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) {
                    final long rowid = cursor.getLong(cursor.getColumnIndex("_id"));

                    view.setOnClickListener(new View.OnClickListener() {
                        long _rowid = rowid;

                        public void onClick(View v) {
                            //do something
                        }
                    });
                    return true;
                }
                return false;
            }
        });



        ListView listView = (ListView) findViewById(R.id.lstUsers);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        getLoaderManager().initLoader(1, null, this);
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
                new String[]{"_id", "name", "gender"}, where, null, null);
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
        //DisplaySetupDialog setupDialog = new DisplaySetupDialog();
        Bundle args = new Bundle();
        args.putLong("rowid", id);

        //setupDialog.setArguments(args);
        //setupDialog.show(getFragmentManager(), "setupDialog");
    }
}
