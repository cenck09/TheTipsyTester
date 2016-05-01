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
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            if(name.length() <= 5) {
                sb.append("\t\t\t\t\t");
            }else if(name.length()>5 && name.length()<10){
                sb.append("\t\t\t\t");
            }else if(name.length()>10 && name.length()<15){
                sb.append("\t\t\t");
            }
            sb.append(test);
            if(test.length() <= 2) {
                sb.append("\t\t\t\t\t\t\t");
            }else if(test.length()>2 && test.length()<6){
                sb.append("\t\t\t\t\t\t");
            }else if(test.length()>6 && test.length()<9){
                sb.append("\t\t\t\t\t");
            }


            sb.append(best);
            if(best.length() <= 2) {
                sb.append("\t\t\t\t\t\t\t");
            }else if(best.length()>2 && best.length()<6){
                sb.append("\t\t\t\t\t\t");
            }else if(best.length()>6 && best.length()<9){
                sb.append("\t\t\t\t\t");
            }


            sb.append(worst);
            sb.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.sortQueryResults)).setText(sb);
    }


    public void btnTestClick(View view) {
        StringBuffer sb = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.test, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            if(name.length() <= 5) {
                sb.append("\t\t\t\t\t");
            }else if(name.length()>5 && name.length()<10){
                sb.append("\t\t\t\t");
            }else if(name.length()>10 && name.length()<15){
                sb.append("\t\t\t");
            }
            sb.append(test);
            sb.append("\t\t\t\t\t");


            sb.append(best);
            if(best.length() <= 2) {
                sb.append("\t\t\t\t\t\t\t");
            }else if(best.length()>2 && best.length()<6){
                sb.append("\t\t\t\t\t\t");
            }else if(best.length()>6 && best.length()<9){
                sb.append("\t\t\t\t\t");
            }


            sb.append(worst);
            sb.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.sortQueryResults)).setText(sb);
    }

    public void btnBestClick(View view) {
        StringBuffer sb = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.best, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            if(name.length() <= 5) {
                sb.append("\t\t\t\t\t");
            }else if(name.length()>5 && name.length()<10){
                sb.append("\t\t\t\t");
            }else if(name.length()>10 && name.length()<15){
                sb.append("\t\t\t");
            }
            sb.append(test);
            sb.append("\t\t\t\t\t");


            sb.append(best);
            if(best.length() <= 2) {
                sb.append("\t\t\t\t\t\t\t");
            }else if(best.length()>2 && best.length()<6){
                sb.append("\t\t\t\t\t\t");
            }else if(best.length()>6 && best.length()<9){
                sb.append("\t\t\t\t\t");
            }


            sb.append(worst);
            sb.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.sortQueryResults)).setText(sb);
    }

    public void btnWorstClick(View view) {
        StringBuffer sb = new StringBuffer();
        Cursor c = theDB.rawQuery("SELECT * FROM users,scores WHERE users._id = scores._id ORDER BY scores.worst, users.name", null);

        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndexOrThrow("name"));
            test = c.getString(c.getColumnIndexOrThrow("test"));
            best = c.getString(c.getColumnIndexOrThrow("best"));
            worst = c.getString(c.getColumnIndexOrThrow("worst"));

            sb.append(name);
            if(name.length() <= 5) {
                sb.append("\t\t\t\t\t");
            }else if(name.length()>5 && name.length()<10){
                sb.append("\t\t\t\t");
            }else if(name.length()>10 && name.length()<15){
                sb.append("\t\t\t");
            }
            sb.append(test);
            sb.append("\t\t\t\t\t");


            sb.append(best);
            if(best.length() <= 2) {
                sb.append("\t\t\t\t\t\t\t");
            }else if(best.length()>2 && best.length()<6){
                sb.append("\t\t\t\t\t\t");
            }else if(best.length()>6 && best.length()<9){
                sb.append("\t\t\t\t\t");
            }


            sb.append(worst);
            sb.append("\n\n\n");
            //sb.append("---------------------------------------------------------------\n");
        }
        c.close();
        ((TextView) findViewById(R.id.sortQueryResults)).setText(sb);
    }
}
