package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class userSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
    }
    public void addUser(View view){
        //When the + button is clicked from user selection page
        Intent intent = new Intent(this, newUserActivity.class);

        startActivity(intent);
    }
}
