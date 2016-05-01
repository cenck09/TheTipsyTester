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

import java.util.ArrayList;

public class scorereportActivity extends AppCompatActivity {
    String prevTest, userName = "";
    ArrayList<String> nextTests;
    int score = 0, best = 0, worst = 0;
    long rowid;
    boolean calibration = false;
    double bac = 0;

    int bacCount, numTests, additive = 0;

    int soberScore = 0,insertValue,newPercent;
    double oldPercent;
    int oldDrunkScore,newDrunkScore;
    double testPercent;
    int displayPercent,storedPercent;

    double performancePercent;
    String bacText = "Calibrate test first";

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
        nextTests = intent.getStringArrayListExtra("nextTests");
        score = intent.getIntExtra("score", 0);
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);
        bacCount = intent.getIntExtra("bacCount", 0);
        numTests = intent.getIntExtra("numTests", 0);

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
                    if (soberScore == 0){
                        soberScore = score;
                    }
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

                    if(prevTest.equals("balance")) {
                        if (ut04Percent == 0) {
                            bacText = "0.00%";
                            bacCount = bacCount + 1;
                            additive = 1;
                        } else if (performancePercent <= ut08Percent - 5) {
                            bacText = "0.00-0.04%";
                            bacCount = bacCount + 1;
                            additive = 1;
                        } else if (performancePercent < ut08Percent) {
                            bacText = "0.04-0.08%";
                            bacCount = bacCount + 2;
                            additive = 2;
                        } else if (performancePercent >= ut08Percent && performancePercent < ut12Percent) {
                            bacText = "0.08-0.12%";
                            bacCount = bacCount + 3;
                            additive = 3;
                        } else if (performancePercent >= ut12Percent && performancePercent < ut16Percent) {
                            bacText = "0.12-0.16%";
                            bacCount = bacCount + 4;
                            additive = 4;
                        } else if (performancePercent >= ut16Percent && performancePercent < ut20Percent) {
                            bacText = "0.16-0.20%";
                            bacCount = bacCount + 5;
                            additive = 5;
                        } else if (performancePercent >= ab20Percent) {
                            bacText = ">0.20%";
                            bacCount = bacCount + 6;
                            additive = 6;
                        }else{
                            bacText = "Calibrate test first";
                        }
                    }else{

                        if (ut04Percent == 0) {
                            bacText = "0.00%";
                            bacCount = bacCount + 1;
                            additive = 1;
                        } else if (performancePercent >= ut08Percent + 5) {
                            bacText = "0.00-0.04%";
                            bacCount = bacCount + 1;
                            additive = 1;
                        } else if (performancePercent > ut08Percent) {
                            bacText = "0.04-0.08%";
                            bacCount = bacCount + 2;
                            additive = 2;
                        } else if (performancePercent <= ut08Percent && performancePercent > ut12Percent) {
                            bacText = "0.08-0.12%";
                            bacCount = bacCount + 3;
                            additive = 3;
                        } else if (performancePercent <= ut12Percent && performancePercent > ut16Percent) {
                            bacText = "0.12-0.16%";
                            bacCount = bacCount + 4;
                            additive = 4;
                        } else if (performancePercent <= ut16Percent && performancePercent > ut20Percent) {
                            bacText = "0.16-0.20%";
                            bacCount = bacCount + 5;
                            additive = 5;
                        } else if (performancePercent <= ab20Percent) {
                            bacText = ">0.20%";
                            bacCount = bacCount + 6;
                            additive = 6;
                        }else{
                            bacText = "Calibrate test first";
                        }

                    }
                }


            }

            selectQuery = "SELECT * FROM " + "scores" + " WHERE _id = " + rowid
            + " AND test = '" + prevTest + "'";
            c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                best = c.getInt(c.getColumnIndex("best"));
                worst = c.getInt(c.getColumnIndex("worst"));
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
            intent.putExtra("bacCount", bacCount-additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        if(prevTest.equals("pattern")) {
            Intent intent = new Intent(scorereportActivity.this, patternTest.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            intent.putExtra("bacCount", bacCount-additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        if(prevTest.equals("typing")) {
            Intent intent = new Intent(scorereportActivity.this, typingTest.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            intent.putExtra("bacCount", bacCount-additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        if(prevTest.equals("schwack")) {
            Intent intent = new Intent(scorereportActivity.this, schwack_a_moleaa.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            intent.putExtra("bacCount", bacCount-additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        if(prevTest.equals("comet")) {
            Intent intent = new Intent(scorereportActivity.this, comet_smash.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            intent.putExtra("bacCount", bacCount-additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        if(prevTest.equals("move_ball")) {
            Intent intent = new Intent(scorereportActivity.this, move_ball.class);
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("calibration", calibration);
            intent.putExtra("bacCount", bacCount - additive);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }
        finish();
    }

    public void nextAction(View view) {
        if (rowid != -1) {

            String selectQuery = "SELECT * FROM " + "scores" + " WHERE _id = " + rowid
                    + " AND test = '" + prevTest + "'";
            Cursor c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                testPercent = ((double)score/(double)soberScore)*100;
                displayPercent = (int) testPercent;

                if(calibration){
                    oldDrunkScore = (int)(soberScore * (oldPercent/100));
                    newDrunkScore = (int)(((double)(oldDrunkScore*3) + (double)score)/4);
                    newPercent = (int)(((double)newDrunkScore/(double)soberScore)*100);
                    storedPercent = newPercent;



                    if(bac >= 0 && bac < 0.04){
                        insertValue = (soberScore*3 + score)/4;
                        ContentValues values = new ContentValues();
                        values.put("ut04", insertValue);
                        System.out.println("Insert Value: " + insertValue);
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
                values.put("ut04", score);
                if(prevTest.equals("balance")) {
                    values.put("ut08", 105);
                    values.put("ut12", 115);
                    values.put("ut16", 130);
                    values.put("ut20", 145);
                    values.put("ab20", 160);
                }
                if(prevTest.equals("pattern")) {
                    values.put("ut08", 95);
                    values.put("ut12", 85);
                    values.put("ut16", 70);
                    values.put("ut20", 55);
                    values.put("ab20", 40);
                }

                if(prevTest.equals("typing")) {
                    values.put("ut08", 95);
                    values.put("ut12", 85);
                    values.put("ut16", 70);
                    values.put("ut20", 55);
                    values.put("ab20", 30);
                }

                if(prevTest.equals("schwack")) {
                    values.put("ut08", 95);
                    values.put("ut12", 90);
                    values.put("ut16", 83);
                    values.put("ut20", 73);
                    values.put("ab20", 60);
                }

                if(prevTest.equals("comet")) {
                    values.put("ut08", 95);
                    values.put("ut12", 90);
                    values.put("ut16", 83);
                    values.put("ut20", 73);
                    values.put("ab20", 60);
                }

                if(prevTest.equals("move_ball")) {
                    values.put("ut08", 95);
                    values.put("ut12", 90);
                    values.put("ut16", 83);
                    values.put("ut20", 73);
                    values.put("ab20", 60);
                }

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
                }else {
                    if (score > best) {
                        //new best score
                        ContentValues values = new ContentValues();
                        values.put("best", score);
                        db.update("scores", values, "_id = ? AND test = ?", new String[]{String.valueOf("" + rowid), String.valueOf("" + prevTest)});
                    }
                    if (score < worst) {
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
        if (nextTests.size() != 0) {
            String next = nextTests.remove(0);
            if(next.equals("balanceTest")) {
                Intent intent = new Intent(this, balanceTest.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }

            if(next.equals("comet_smash")) {
                Intent intent = new Intent(this, comet_smash.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }

            if(next.equals("move_ball")) {
                Intent intent = new Intent(this, move_ball.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }

            if(next.equals("patternTest")) {
                Intent intent = new Intent(this, patternTest.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }

            if(next.equals("schwack")) {
                Intent intent = new Intent(this, schwack_a_moleaa.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }

            if (next.equals("typingTest")) {
                Intent intent = new Intent(this, typingTest.class);
                intent.putStringArrayListExtra("nextTests", nextTests);
                intent.putExtra("bacCount", bacCount);
                intent.putExtra("numTests", numTests);
                startActivity(intent);
            }


        } else {



            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            System.out.println("bacCOUNT: " + bacCount + "\n\n\n");

            if(numTests > 1){
                Intent intent = new Intent(this, estimatedBAC.class);
                intent.putExtra("bacCount", bacCount);
                if(bacText.equals("Calibrate test first")){
                    intent.putExtra("numTests", numTests-1);
                }else{
                    intent.putExtra("numTests", numTests);
                }
                startActivity(intent);
            }
            finish();
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
