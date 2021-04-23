package me.foolishchow.androidplugins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import me.foolishchow.android.annotation.IntentParam;


public class MainActivity extends AppCompatActivity {

    @IntentParam
    List<Integer> ints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Class<MainActivity$ShadowLifeCycleDelegate> classes =
        //        MainActivity$ShadowLifeCycleDelegate.class;
        //mKernalViewBinding.mNavTitle.setText("sdasdasdasd");
        //setContentView(R.layout.activity_main);
        //MainActivity$ShadowLifeCycleDelegate mainActivity$ShadowLifeCycleDelegate = new MainActivity$ShadowLifeCycleDelegate();
        //mainActivity$ShadowLifeCycleDelegate.restoreInstanceState();
    }


}