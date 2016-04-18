package com.thetipsytester.thetipsytester;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class userSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TipsyDB tipsy;
    SQLiteDatabase db;
    Cursor userCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        populateList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
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

    public void populateList(){
        tipsy = new TipsyDB(this);
        db = tipsy.getWritableDatabase();
        userCursor = db.rawQuery("SELECT * FROM users", null);
        Log.i("SOMETHING", "Cursor(0)" + userCursor.getColumnName(0));


        ListView listView = (ListView) findViewById(R.id.lstUsers);
        UserCursorAdapter userAdapter = new UserCursorAdapter(this, userCursor,0);
        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener(this);

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putLong("rowid", id).apply();
        if (getIntent().getStringExtra("activity") != null && getIntent().getStringExtra("activity").equals("BAC")) {
            Intent intent = new Intent(this, bacCalculatorActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }
}
