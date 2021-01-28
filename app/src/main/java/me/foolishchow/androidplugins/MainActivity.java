package me.foolishchow.androidplugins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;

import me.foolishchow.android.annotation.LayoutId;
import me.foolishchow.android.intentBuilder.LifeCycleHelper;


@LayoutId(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {



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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        LifeCycleHelper.saveInstanceState(this,outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LifeCycleHelper.restoreInstanceState(this,savedInstanceState);
    }
}