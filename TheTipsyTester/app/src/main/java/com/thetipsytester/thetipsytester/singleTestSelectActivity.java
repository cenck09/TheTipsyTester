package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class singleTestSelectActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletest_select);
    }

    public void startBalance(View view){
        //When the Baseline button is clicked from the main menu
        Intent intent = new Intent(this, balanceTest.class);

        startActivity(intent);
    }

}
