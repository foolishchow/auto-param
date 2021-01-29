package me.foolishchow.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/11/2020 9:17 AM
 */
public class IntentBuilder {

    private Context mContext;
    protected Intent mIntent;

    public IntentBuilder setIntent(Intent intent) {
        mIntent = intent;
        return this;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public IntentBuilder setContext(Context context) {
        mContext = context;
        return this;
    }

    public IntentBuilder setFlags(int flags) {
        mIntent.setFlags(flags);
        return this;
    }

    public void start() {
        mContext.startActivity(mIntent);
    }

    public void start(@Nullable Bundle options) {
        mContext.startActivity(mIntent,options);
    }

    public void start(int requestCode) {
        ((Activity) mContext).startActivityForResult(mIntent, requestCode);
    }

}
