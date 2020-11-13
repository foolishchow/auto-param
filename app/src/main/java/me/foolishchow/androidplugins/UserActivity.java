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

    public static class UserInfo implements Serializable {

    }

    @IntentParam
    byte mIsByte;

    @IntentParam
    Map<String,BigDecimal> map;

    @IntentParam
    Set<String> set;


    @IntentParam
    List<BigDecimal> BigDecimal;
    @IntentParam
    List<Parcelable> pracelables;

    @IntentParam
    ArrayList<Parcelable> pracelableArrayList;

    @IntentParam
    List<String> name;

    @IntentParam
    ArrayList<String> nameList;


    @IntentParam
    List<CharSequence> charSequence;

    @IntentParam
    ArrayList<CharSequence> charSequenceList;


    @InstanceState(persist = true)
    @IntentParam
    String mIsString;

    @IntentParam
    protected int mIsInt;

    @IntentParam
    Integer mIsInteger;

    @IntentParam
    int[] mIsIntArray;

    @IntentParam
    Integer[] mIsIntegerArray;


    @IntentParam
    long[] mIslongArray;

    @IntentParam
    Long[] mIsLongArray;

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

        Intent intent = new Intent(this, UserActivity.class);
        //int
        intent.putExtra("",1);
        intent.putExtra("",Integer.valueOf(1));
        intent.getIntExtra("",Integer.MIN_VALUE);
        //int[]
        intent.putExtra("",new int[]{1,2});
        intent.putExtra("",new Integer[]{1,2});
        int[] intArrayExtra = intent.getIntArrayExtra("");
        //intent.putCharSequenceArrayListExtra()
        //intent.put

        intent.putExtra("","");
        intent.putExtra("",new UserInfo());


        intent.getBooleanExtra("",false);
        intent.getBooleanArrayExtra("");

        intent.getByteExtra("",Byte.MIN_VALUE);
        intent.getByteArrayExtra("");



        intent.getBundleExtra("");



    }
}