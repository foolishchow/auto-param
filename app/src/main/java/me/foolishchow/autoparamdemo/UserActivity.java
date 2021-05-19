package me.foolishchow.autoparamdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import me.foolishchow.android.annotation.IntentParam;

public class UserActivity extends AppCompatActivity {

    String staticUserName;

    String[] staticUserNames;

    int staticUserAge;

    int[] staticUserAges;

    @IntentParam
    String userName;
    Integer name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


    }
}