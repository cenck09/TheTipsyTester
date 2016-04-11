package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class testSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_select);

    }

    public void single(View view) {
        //When the single test button is clicked from the test select menu
        Intent intent = new Intent(this, singleTestSelectActivity.class);

        startActivity(intent);
    }

    public void quick(View view) {

    }

    public void all(View view) {

    }


}
