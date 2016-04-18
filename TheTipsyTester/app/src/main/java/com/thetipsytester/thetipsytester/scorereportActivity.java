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
    boolean playedBefore;
    boolean calibration = false;
    double bac = 0;

    int soberScore = 0;
    int insertValue;
    int newPercent;
    int oldPercent;
    int oldDrunkScore;
    int newDrunkScore;

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
        playedBefore = sharedPref.getBoolean(prevTest, false);

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

            }

            selectQuery = "SELECT * FROM " + "scores" + " WHERE _id = " + rowid
            + " AND test = '" + prevTest + "'";
            c = db.rawQuery(selectQuery, null);

            if (c != null && c.moveToFirst()) {
                best = c.getInt(c.getColumnIndex("best"));
                worst = c.getInt(c.getColumnIndex("worst"));

                if(calibration){
                    oldDrunkScore = soberScore*oldPercent;
                    newDrunkScore = (oldDrunkScore*5 + score)/6;
                    newPercent = (newDrunkScore/soberScore)*100;

                    if(bac >= 0 && bac < 0.04){
                        insertValue = (soberScore*5 + score)/6;
                        System.out.println("SOBER SCORE: " + insertValue);
                        ContentValues values = new ContentValues();
                        values.put("ut04", insertValue);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.04 && bac < 0.08){
                        ContentValues values = new ContentValues();
                        values.put("ut08", newPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});
                    }else if(bac >= 0.08 && bac < 0.12){
                        ContentValues values = new ContentValues();
                        values.put("ut12", newPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.12 && bac < 0.16){
                        ContentValues values = new ContentValues();
                        values.put("ut16", newPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.16 && bac < 0.20){
                        ContentValues values = new ContentValues();
                        values.put("ut20", newPercent);
                        db.update("tests", values, "_id = ? AND test = ?", new String[]{String.valueOf(""
                                + rowid), String.valueOf("" + prevTest)});

                    }else if(bac >= 0.2){
                        ContentValues values = new ContentValues();
                        values.put("ab20", newPercent);
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
            TextView tvPfSS = (TextView)findViewById(R.id.scoresPfSSField);


            tvTest.setText(prevTest.toUpperCase());
            tvUser.setText(userName);
            tvScore.setText(Integer.toString(score));
            tvBest.setText(Integer.toString(best));
            tvWorst.setText(Integer.toString(worst));

            if(calibration){
                if(bac > 0) {
                    String retVal = Double.toString(bac);
                    retVal = retVal.substring(0, 5);
                    tvBAC.setText(retVal + "%");
                }else{
                    tvBAC.setText("0.00");
                }
                tvPfSS.setText("" + newPercent);
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

        sharedPref.edit().putBoolean(prevTest, true).commit();

        //check to see if nextTests is empty, if so, go to final report screen
        //else go to next test
        if(nextTests != null && nextTests.length > 0){
            //go to next test
        }else{

            if(mInterstitialAd.isLoaded()){
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
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }
}
