package com.thetipsytester.thetipsytester;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class scoresActivity extends AppCompatActivity {

    TipsyDB tipsy;
    SQLiteDatabase theDB;
    String name, test, best, worst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        tipsy = new TipsyDB(this);
        theDB = tipsy.getWritableDatabase();
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }


    public void btnUserClick(View view) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            sb.append("\n\n\n");

            sb2.append(test);
            sb2.append("\n\n\n");

            sb3.append(best);
            sb3.append("\n\n\n");

            sb4.append(worst);
            sb4.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.userInfoScoreField)).setText(sb);
        ((TextView) findViewById(R.id.testInfoScoreField)).setText(sb2);
        ((TextView) findViewById(R.id.bestInfoScoreField)).setText(sb3);
        ((TextView) findViewById(R.id.worstInfoScoreField)).setText(sb4);
    }


    public void btnTestClick(View view) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.test, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            sb.append("\n\n\n");

            sb2.append(test);
            sb2.append("\n\n\n");

            sb3.append(best);
            sb3.append("\n\n\n");

            sb4.append(worst);
            sb4.append("\n\n\n");
        }
        c.close();
        ((TextView) findViewById(R.id.userInfoScoreField)).setText(sb);
        ((TextView) findViewById(R.id.testInfoScoreField)).setText(sb2);
        ((TextView) findViewById(R.id.bestInfoScoreField)).setText(sb3);
        ((TextView) findViewById(R.id.worstInfoScoreField)).setText(sb4);
    }

    public void btnBestClick(View view) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.best DESC, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            sb.append("\n\n\n");

            sb2.append(test);
            sb2.append("\n\n\n");

            sb3.append(best);
            sb3.append("\n\n\n");

            sb4.append(worst);
            sb4.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.userInfoScoreField)).setText(sb);
        ((TextView) findViewById(R.id.testInfoScoreField)).setText(sb2);
        ((TextView) findViewById(R.id.bestInfoScoreField)).setText(sb3);
        ((TextView) findViewById(R.id.worstInfoScoreField)).setText(sb4);
    }

    public void btnWorstClick(View view) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.worst, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            sb.append("\n\n\n");

            sb2.append(test);
            sb2.append("\n\n\n");

            sb3.append(best);
            sb3.append("\n\n\n");

            sb4.append(worst);
            sb4.append("\n\n\n");
        }
        c.close();
        ((TextView) findViewById(R.id.userInfoScoreField)).setText(sb);
        ((TextView) findViewById(R.id.testInfoScoreField)).setText(sb2);
        ((TextView) findViewById(R.id.bestInfoScoreField)).setText(sb3);
        ((TextView) findViewById(R.id.worstInfoScoreField)).setText(sb4);
    }
}
