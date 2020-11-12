package me.foolishchow.androidplugins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.android.annotation.IntentParam;

public class UserActivity extends AppCompatActivity {

    @InstanceState(persist = true)
    @IntentParam
    String mIsString;


    @IntentParam
    int mIsInt;


    @IntentParam
    HashMap<String,String> mIsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }
}