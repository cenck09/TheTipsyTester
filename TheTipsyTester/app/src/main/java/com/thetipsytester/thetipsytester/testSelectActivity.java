package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class testSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_select);

    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void single(View view) {
        //When the single test button is clicked from the test select menu
        Intent intent = new Intent(this, singleTestSelectActivity.class);
        intent.putExtra("bacCount", 0);
        intent.putExtra("numTests", 1);

        startActivity(intent);
    }

    public void quick(View view) {
        int first, second = -1, third = -1;
        int numTests = getResources().getStringArray(R.array.test_names).length;
        Random random = new Random();
        first = random.nextInt(numTests);
        while(second == -1 || second == first){
            second = random.nextInt(numTests);
        }
        while(third == -1 || third == first || third == second){
            third = random.nextInt(numTests);
        }
        String[] testArray = getResources().getStringArray(R.array.test_names);
        ArrayList<String> nextTests = new ArrayList<String>();
        nextTests.add(testArray[third]);
        nextTests.add(testArray[second]);



        if(testArray[first].equals("balanceTest")) {
            Intent intent = new Intent(this, balanceTest.class);
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("bacCount", 0);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }

        if(testArray[first].equals("comet_smash")) {
            Intent intent = new Intent(this, comet_smash.class);
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("bacCount", 0);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }

        if(testArray[first].equals("patternTest")) {
            Intent intent = new Intent(this, patternTest.class);
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("bacCount", 0);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }

        if(testArray[first].equals("schwack_a_moleaa")) {
            Intent intent = new Intent(this, schwack_a_moleaa.class);
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("bacCount", 0);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }

        if(testArray[first].equals("typingTest")) {
            Intent intent = new Intent(this, typingTest.class);
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("bacCount", 0);
            intent.putExtra("numTests", numTests);
            startActivity(intent);
        }



    }

    public void all(View view) {

        int numTests = getResources().getStringArray(R.array.test_names).length;
        String[] testArray = getResources().getStringArray(R.array.test_names);
        ArrayList<String> nextTests = new ArrayList<String>();

        for(int i = 1; i<numTests; i++){
            nextTests.add(testArray[i]);
        }

        Intent intent = new Intent(this, balanceTest.class);
        intent.putStringArrayListExtra("nextTests", nextTests);
        intent.putExtra("bacCount", 0);
        intent.putExtra("numTests", numTests);
        startActivity(intent);

    }


}
