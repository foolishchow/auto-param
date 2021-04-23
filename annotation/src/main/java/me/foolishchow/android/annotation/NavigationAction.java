package me.foolishchow.android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Author: foolishchow
 * Date: 23/4/2021 9:50 PM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NavigationAction {
    String name();
    String description() default "";
    int actionId() default -1;
}
