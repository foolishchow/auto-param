package me.foolishchow.anrdoid.processor.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.io.Serializable;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 1:14 PM <br/>
 */
public class TypeNames {
    public static final TypeName String = ClassName.get("java.lang", "String");

    public static final TypeName CONTEXT = ClassName.get("android.content", "Context");

    public static final TypeName INTENT = ClassName.get("android.content", "Intent");

    public final static String PARCELABLE = "android.os.Parcelable";

    public final static String SERIALIZABLE = Serializable.class.getCanonicalName();

}
