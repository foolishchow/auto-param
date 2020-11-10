package me.foolishchow.android.annotation;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 10/11/2020 5:13 PM <br/>
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface LayoutId {
    int value();
}
