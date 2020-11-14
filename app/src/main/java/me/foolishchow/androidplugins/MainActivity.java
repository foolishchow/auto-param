package me.foolishchow.androidplugins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;

import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.android.annotation.IntentParam;
import me.foolishchow.android.annotation.LayoutId;
import me.foolishchow.android.utils.LifeCycleHelper;
import me.foolishchow.androidplugins.databinding.ActivityMainBinding;
import me.foolishchow.androidplugins.fake.BaseActivity;


@LayoutId(R.layout.activity_main)
public class MainActivity extends BaseActivity<ActivityMainBinding> {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Class<MainActivity$ShadowLifeCycleDelegate> classes =
        //        MainActivity$ShadowLifeCycleDelegate.class;
        mKernalViewBinding.mNavTitle.setText("sdasdasdasd");
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