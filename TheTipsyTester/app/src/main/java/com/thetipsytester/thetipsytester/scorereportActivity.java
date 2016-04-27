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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class scorereportActivity extends AppCompatActivity {
    String prevTest, userName = "";
    String[] nextTests;
    int score = 0, best = 0, worst = 0;
    long rowid;
    boolean calibration = false;
    double bac = 0;

    int soberScore = 0;
    int insertValue;
    int newPercent;
    double oldPercent;
    int oldDrunkScore;
    int newDrunkScore;
    double testPercent;
    int displayPercent;
    int storedPercent;

    double performancePercent;
    String bacText;

    TipsyDB tipsy;
    SQLiteDatabase db;
    SharedPreferences sharedPref;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorereport);

        Intent intent = getIntent();
        prevTest = intent.getStringExtra("prevTest");
        nextTests = intent.getStringArrayExtra("nextTests");
        score = intent.getIntExtra("score", 0);
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);

        System.out.println("Calibraion: " + calibration);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                //go to estimatedBAC screen/home
                Intent intent = new Intent(scorereportActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        requestNewInterstitial();


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        rowid = sharedPref.getLong("rowid", -1);

        if (rowid != -1) {
            tipsy = new TipsyDB(this);
            db = tipsy.getWritableDatabase();

            String selectQuery = "SELECT * FROM " + "users" + " WHERE _id = " + rowid;
            Cursor c;
            c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                userName = c.getString(c.getColumnIndex("name"));
            }



            if(calibration) {
                selectQuery = "SELECT * FROM " + "tests" + " WHERE _id = " + rowid
                        + " AND test = '" + prevTest + "'";
                c = db.rawQuery(selectQuery, null);

                if (c != null && c.moveToFirst()) {
                    soberScore = c.getInt(c.getColumnIndex("ut04"));
                    if(bac >= 0.04 && bac < 0.08){
                        oldPercent = c.getInt(c.getColumnIndex("ut08"));
                    }else if(bac >= 0.08 && bac < 0.12){
                        oldPercent = c.getInt(c.getColumnIndex("ut12"));
                    }else if(bac >= 0.12 && bac < 0.16){
                        oldPercent = c.getInt(c.getColumnIndex("ut16"));
                    }else if(bac >= 0.16 && bac < 0.20){
                        oldPercent = c.getInt(c.getColumnIndex("ut20"));
                    }else if(bac >= 0.2){
                        oldPercent = c.getInt(c.getColumnIndex("ab20"));
                    }

                } else {
                    soberScore = score;
                }
            }else{
                //not calibration
                selectQuery = "SELECT * FROM " + "tests" + " WHERE _id = " + rowid
                        + " AND test = '" + prevTest + "'";
                c = db.rawQuery(selectQuery, null);

                if (c != null && c.moveToFirst()) {
                    soberScore = c.getInt(c.getColumnIndex("ut04"));
                    performancePercent = ((double) score / (double) soberScore) * 100;
                    System.out.println("PerformancePercent: " + performancePercent);
                    int ut04Percent = c.getInt(c.getColumnIndex("ut04"));
                    System.out.println("UT04: " + ut04Percent);
                    int ut08Percent = c.getInt(c.getColumnIndex("ut08"));
                    System.out.println("UT08: " + ut08Percent);
                    int ut12Percent = c.getInt(c.getColumnIndex("ut12"));
                    System.out.println("UT12: " + ut12Percent);
                    int ut16Percent = c.getInt(c.getColumnIndex("ut16"));
                    System.out.println("UT16: " + ut16Percent);
                    int ut20Percent = c.getInt(c.getColumnIndex("ut20"));
                    System.out.println("UT20: " + ut20Percent);
                    int ab20Percent = c.getInt(c.getColumnIndex("ab20"));
                    System.out.println("AB20: " + ab20Percent);
                    if(ut04Percent == 0){
                        bacText = "0.00%";
                    }else if(performancePercent< ut08Percent-5){
                        bacText = "0.00-0.04%";
                    }else if (performancePercent < ut08Percent) {
                        bacText = "0.04-0.08%";
                    } else if (performancePercent >= ut08Percent && performancePercent < ut12Percent) {
                        bacText = "0.08-0.12%";
                    } else if (performancePercent >= ut12Percent && performancePercent < ut16Percent) {
                        bacText = "0.12-0.16%";
                    } else if (performancePercent >= ut16Percent && performancePercent < ut20Percent) {
                        bacText = "0.16-0.20%";
                    } else if (performancePercent >= ab20Percent) {
                        bacText = ">0.20%";
                    }
                }


            }

            selectQuery = "SELECT * FROM " + "scores" + " WHERE _id = " + rowid
            + " AND test = '" + prevTest + "'";
            c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                best = c.getInt(c.getColumnIndex("best"));
                worst = c.getInt(c.getColumnIndex("worst"));
                testPercent = ((double)score/(double)soberScore)*100;
                displayPercent = (int) testPercent;

                if(calibration){
                    oldDrunkScore = (int)(soberScore * (oldPercent/100));
                    newDrunkScore = (int)(((double)(oldDrunkScore*5) + (double)score)/6);
                    newPercent = (int)(((double)newDrunkScore/(double)soberScore)*100);
                    storedPercent = newPercent;

                    if(bac >= 0 && bac < 0.04){
                        insertValue = (soberScore*5 + score)/6;
                        ContentValues values = new ContentValues();
                        values.put("ut04", insertValue);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.04 && bac < 0.08){
                        ContentValues values = new ContentValues();
                        values.put("ut08", storedPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});
                    }else if(bac >= 0.08 && bac < 0.12){
                        ContentValues values = new ContentValues();
                        values.put("ut12", storedPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.12 && bac < 0.16){
                        ContentValues values = new ContentValues();
                        values.put("ut16", storedPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.16 && bac < 0.20){
                        ContentValues values = new ContentValues();
                        values.put("ut20", storedPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.2){
                        ContentValues values = new ContentValues();
                        values.put("ab20", storedPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});
                    }
                }


            }

            c.close();

            TextView tvTest = (TextView)findViewById(R.id.scoreTestName);
            TextView tvUser = (TextView)findViewById(R.id.scoreUserField);
            TextView tvScore = (TextView)findViewById(R.id.scoreScoreField);
            TextView tvBest = (TextView)findViewById(R.id.scoreBestField);
            TextView tvWorst = (TextView)findViewById(R.id.scoreWorstField);
            TextView tvBAC = (TextView)findViewById(R.id.scoreBACField);



            tvTest.setText(prevTest.toUpperCase());
            tvUser.setText(userName);
            tvScore.setText(Integer.toString(score));
            if(best == 0){
                tvBest.setText(Integer.toString(score));
            }else{
                tvBest.setText(Integer.toString(best));
            }
            if(worst == 0){
                tvWorst.setText(Integer.toString(score));
            }else{
                tvWorst.setText(Integer.toString(worst));
            }

            if(calibration){
                if(bac > 0) {
                    String retVal = Double.toString(bac);
                    retVal = retVal.substring(0, 5);
                    tvBAC.setText(retVal + "%");
                }else{
                    tvBAC.setText("0.00%");
                }

            }else{
                tvBAC.setText(bacText);
            }


        }else {
            TextView tvTest = (TextView)findViewById(R.id.scoreTestName);
            TextView tvUser = (TextView)findViewById(R.id.scoreUserField);
            TextView tvScore = (TextView)findViewById(R.id.scoreScoreField);
            TextView tvBest = (TextView)findViewById(R.id.scoreBestField);
            TextView tvWorst = (TextView)findViewById(R.id.scoreWorstField);


            tvTest.setText(prevTest.toUpperCase());
            tvUser.setText("No user selected");
            tvScore.setText(Integer.toString(score));
            tvBest.setText(Integer.toString(best));
            tvWorst.setText(Integer.toString(worst));
        }
    }


    public void retryTest(View view){
        if(prevTest.equals("balance")) {
            Intent intent = new Intent(scorereportActivity.this, balanceTest.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            startActivity(intent);
        }
        if(prevTest.equals("pattern")) {
            Intent intent = new Intent(scorereportActivity.this, patternTest.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            startActivity(intent);
        }
        if(prevTest.equals("typing")) {
            Intent intent = new Intent(scorereportActivity.this, typingTest.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            startActivity(intent);
        }
        if(prevTest.equals("schwack")) {
            Intent intent = new Intent(scorereportActivity.this, schwack_a_moleaa.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            startActivity(intent);
        }
        if(prevTest.equals("comet")) {
            Intent intent = new Intent(scorereportActivity.this, comet_smash.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            startActivity(intent);
        }
    }

    public void nextAction(View view) {
        if (rowid != -1) {

            if (best == 0 && worst == 0) {
                ContentValues values = new ContentValues();
                values.put("_id", rowid);
                values.put("test", prevTest);
                values.put("best", score);
                values.put("worst", score);
                db.insert("scores", null, values);

                values = new ContentValues();
                values.put("_id", rowid);
                values.put("test", prevTest);

                values.put("ut08", 105);
                values.put("ut12", 110);
                values.put("ut16", 115);
                values.put("ut20", 120);
                values.put("ab20", 125);
                db.insert("tests", null, values);

            } else {

                if (prevTest.equals("balance")) {
                    if (score < best) {
                        //new best score
                        ContentValues values = new ContentValues();
                        values.put("best", score);
                        db.update("scores", values, "_id = ? AND test = ?", new String[]{String.valueOf("" + rowid), String.valueOf("" + prevTest)});
                    }
                    if (score > worst) {
                        //new worst score
                        ContentValues values = new ContentValues();
                        values.put("worst", score);
                        db.update("scores", values, "_id = ? AND test = ?", new String[]{String.valueOf("" + rowid), String.valueOf("" + prevTest)});
                    }
                }
            }
        }

        //check to see if nextTests is empty, if so, go to final report screen
        //else go to next test
        if (nextTests != null && nextTests.length > 0) {
            //go to next test
        } else {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }
}
