package com.thetipsytester.thetipsytester;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class scorereportActivity extends AppCompatActivity {
    String prevTest, userName = "";
    String[] nextTests;
    int score = 0, best = 0, worst = 0;
    long rowid;
    boolean playedBefore;

    TipsyDB tipsy;
    SQLiteDatabase db;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorereport);

        Intent intent = getIntent();
        prevTest = intent.getStringExtra("prevTest");
        nextTests = intent.getStringArrayExtra("nextTests");
        score = intent.getIntExtra("score", 0);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        rowid = sharedPref.getLong("rowid", -1);
        playedBefore = sharedPref.getBoolean(prevTest, false);

        if (rowid != -1) {
            tipsy = new TipsyDB(this);
            db = tipsy.getWritableDatabase();

            String selectQuery = "SELECT * FROM " + "users" + " WHERE _id = " + rowid;
            Cursor c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {

                userName = c.getString(c.getColumnIndex("name"));
            }

            selectQuery = "SELECT * FROM " + "scores" + " WHERE _id = " + rowid
            + " AND test = '" + prevTest + "'";
            c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                best = c.getInt(c.getColumnIndex("best"));
                worst = c.getInt(c.getColumnIndex("worst"));
            }

            TextView tvTest = (TextView)findViewById(R.id.scoreTestName);
            TextView tvUser = (TextView)findViewById(R.id.scoreUserField);
            TextView tvScore = (TextView)findViewById(R.id.scoreScoreField);
            TextView tvBest = (TextView)findViewById(R.id.scoreBestField);
            TextView tvWorst = (TextView)findViewById(R.id.scoreWorstField);


            tvTest.setText(prevTest.toUpperCase());
            tvUser.setText(userName);
            tvScore.setText(Integer.toString(score));
            tvBest.setText(Integer.toString(best));
            tvWorst.setText(Integer.toString(worst));


        }
    }


    public void retryTest(View view){
        if(prevTest.equals("balance")) {
            Intent intent = new Intent(scorereportActivity.this, balanceTest.class);
            intent.putExtra("nextTests", nextTests);
            startActivity(intent);
        }
    }

    public void nextAction(View view) {
        if (!playedBefore) {
            ContentValues values = new ContentValues();
            values.put("_id", rowid);
            values.put("test", prevTest);
            values.put("best", score);
            values.put("worst", score);
            db.insert("scores", null, values);
        } else {

            if (prevTest.equals("balance")) {
                if (score < best) {
                    //new best score
                    ContentValues values = new ContentValues();
                    values.put("best", score);
                    //db.update("scores", values, "_id = " + rowid + " AND test = '" + prevTest+"'", new String[]{String.valueOf(rowid)});
                }
                if (score > worst) {
                    //new worst score
                    ContentValues values = new ContentValues();
                    values.put("worst", score);
                    //db.update("scores", values, "_id = " + rowid + " AND test = '" + prevTest+"'", new String[]{String.valueOf(rowid)});

                }
            }
        }





        //check to see if nextTests is empty, if so, go to final report screen
        //else go to next test


        sharedPref.edit().putBoolean(prevTest, true);

    }


    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }
}
