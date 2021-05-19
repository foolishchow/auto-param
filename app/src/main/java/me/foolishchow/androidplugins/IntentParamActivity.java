package me.foolishchow.androidplugins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.Serializable;
import java.util.List;

import me.foolishchow.android.annotation.IntentParam;
import me.foolishchow.autoparamdemo.R;

public class IntentParamActivity extends AppCompatActivity {
    public static class UserInfo implements Serializable {

    }
    @IntentParam
    String userName;

    @IntentParam
    int userAge;

    @IntentParam
    String[] selectedHabits;

    @IntentParam
    List<String> followerNames;

    @IntentParam
    List<UserInfo> userInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_param);
    }
}