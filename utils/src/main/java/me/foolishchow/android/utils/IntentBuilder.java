package me.foolishchow.android.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 9:17 AM <br/>
 */
public class IntentBuilder{

    public Context mContext;
    public Intent mIntent;

    public IntentBuilder setIntent(Intent intent){
        mIntent = intent;
        return this;
    }

    public IntentBuilder setContext(Context context){
        mContext = context;
        return this;
    }

    public void build(){
        mContext.startActivity(mIntent);
    }
}
