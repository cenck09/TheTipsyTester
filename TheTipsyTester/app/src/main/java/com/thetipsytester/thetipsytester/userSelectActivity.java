package com.thetipsytester.thetipsytester;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class userSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private TipsyDB tipsy;
    SQLiteDatabase db;
    Cursor userCursor;
    boolean calibration;
    String activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        Intent intent = getIntent();
        calibration = intent.getBooleanExtra("calibration", false);
        activity = intent.getStringExtra("activity");

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
        intent.putExtra("calibration", calibration);
        intent.putExtra("activity", activity);
        System.out.println("USER SELECT ACTIVITY: " + activity);


        startActivity(intent);
    }

    public void populateList(){
        tipsy = new TipsyDB(this);
        db = tipsy.getWritableDatabase();
        userCursor = db.rawQuery("SELECT * FROM users ORDER BY name", null);

        ListView listView = (ListView) findViewById(R.id.lstUsers);
        UserCursorAdapter userAdapter = new UserCursorAdapter(this, userCursor,0);
        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

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
            intent.putExtra("calibration", calibration);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Delete User");

        alertDialogBuilder.setMessage("Are you sure you want to delete + " + id + "?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //delete all records of user

                        String table = "scores";
                        String whereClause = "_id" + "=?";
                        String[] whereArgs = new String[] { String.valueOf(id) };
                        db.delete(table, whereClause, whereArgs);
                        table = "tests";
                        db.delete(table, whereClause, whereArgs);
                        table = "users";
                        db.delete(table, whereClause, whereArgs);


                        populateList();

                        System.out.println("DELETED");

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        return true;
    }
}
