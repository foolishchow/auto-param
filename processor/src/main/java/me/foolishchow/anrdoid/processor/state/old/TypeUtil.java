package me.foolishchow.anrdoid.processor.state.old;

import com.squareup.javapoet.ClassName;

public class TypeUtil {


    public static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static final ClassName PERSISTABLE_BUNDLE = ClassName.get("android.os", "PersistableBundle");
    public static final ClassName IHELPER = ClassName.get("me.foolishchow.android.utils.helpers",
            "IInstanceStateHelper");
}
