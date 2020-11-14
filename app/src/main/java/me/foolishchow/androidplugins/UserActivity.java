package me.foolishchow.androidplugins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.android.annotation.IntentParam;

public class UserActivity extends AppCompatActivity {

    @InstanceState(persist = true)
    String staticUserName;

    @InstanceState(persist = true)
    String[] staticUserNames;

    @InstanceState(persist = true)
    int staticUserAge;

    @InstanceState(persist = true)
    int[] staticUserAges;

    @InstanceState()
    String userName;
    @InstanceState
    Integer name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


    }
}