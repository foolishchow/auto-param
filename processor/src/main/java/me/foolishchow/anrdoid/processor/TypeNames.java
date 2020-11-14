package me.foolishchow.anrdoid.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.io.Serializable;
import java.util.Set;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 1:14 PM <br/>
 */
public class TypeNames {

    public final static ClassName CharSequence = ClassName.get("java.lang", "CharSequence");
    public final static ClassName SET = ClassName.get("java.util", "Set");
    public final static ClassName HASH_SET = ClassName.get("java.util", "HashSet");
    public final static ClassName MAP = ClassName.get("java.util", "Map");
    public final static ClassName HASH_MAP = ClassName.get("java.util", "HashMap");
    public final static ClassName LIST = ClassName.get("java.util", "List");
    public final static ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    public final static ClassName SIZE = ClassName.get("android.util", "Size");// "android.util.Size";

    public final static ClassName SIZEF = ClassName.get("android.util", "SizeF");//"android.util.SizeF";

    public static final ClassName STRING = ClassName.get("java.lang", "String");

    public static final ClassName CONTEXT = ClassName.get("android.content", "Context");

    public static final ClassName INTENT = ClassName.get("android.content", "Intent");

    public final static ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
    //"android.os.Parcelable";

    public final static ClassName SERIALIZABLE = ClassName.get("java.io", "Serializable");
    public static final ClassName INTEGER = ClassName.get("java.lang", "Integer");
    ;
    //Serializable.class.getCanonicalName();

}
