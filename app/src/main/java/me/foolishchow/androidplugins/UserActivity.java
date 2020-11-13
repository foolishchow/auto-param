package me.foolishchow.androidplugins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.android.annotation.IntentParam;

public class UserActivity extends AppCompatActivity {

    public static class UserInfo implements Serializable {

    }

    @InstanceState(persist = true)
    @IntentParam
    String mIsString;

    @IntentParam
    protected int mIsInt;

    @IntentParam
    Integer mIsInteger;

    @IntentParam
    UserInfo mIsBean;

    @IntentParam
    HashMap<String,String> mIsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        UserActivityJumper.with(this)
                .IsInt(1)
                .build();
    }
}