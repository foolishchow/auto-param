package me.foolishchow.anrdoid.processor.intent;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 1:35 PM <br/>
 */
public class IntentTypeUtils {


    public static boolean isSimpleExtra(TypeName typeName) {
        if(typeName.isPrimitive()) return true;
        if(typeName.isBoxedPrimitive()) return true;
        return false;
    }

    public static boolean isArrayExtra(TypeName typeName) {
        if(typeName instanceof ArrayTypeName){
            ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
            if(arrayTypeName.componentType.isBoxedPrimitive() || arrayTypeName.componentType.isPrimitive()){
                return true;
            }
        }
        return false;
    }


    public static boolean isSeriable(TypeName typeName) {
        if(typeName.isPrimitive()) return true;
        if(typeName.isBoxedPrimitive()) return true;
        return false;
    }
}
