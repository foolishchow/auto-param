package me.foolishchow.android.utils.helpers;

import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 9/11/2020 1:50 PM <br/>
 */
public interface IInstanceStateHelper<T> {
    void saveInstanceState(Bundle outState, PersistableBundle outPersistentState, T clazz);
    void restoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState, T clazz);
}
