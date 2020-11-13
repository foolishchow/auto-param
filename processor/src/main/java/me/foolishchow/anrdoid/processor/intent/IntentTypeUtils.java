package me.foolishchow.anrdoid.processor.intent;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 1:35 PM <br/>
 */
public class IntentTypeUtils {

    public static boolean isArray(TypeName typeName) {
        return typeName instanceof ArrayTypeName;
    }

    public static boolean isInt(TypeName typeName) {
        return typeName.equals(TypeName.INT);
    }

    public static boolean isBoxedInt(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.INT);
    }

    public static boolean isIntArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isInt(arrayTypeName.componentType);
    }

    public static boolean isBoxedIntArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedInt(arrayTypeName.componentType);
    }

    public static boolean isBoolean(TypeName typeName) {
        return typeName.equals(TypeName.BOOLEAN);
    }

    public static boolean isBoxedBoolean(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.BOOLEAN);
    }


    public static boolean isByte(TypeName typeName) {
        return typeName.equals(TypeName.BYTE);
    }

    public static boolean isBoxedByte(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.BYTE);
    }

    public static boolean isByteArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isByte(arrayTypeName.componentType);
    }

    public static boolean isBoxedByteArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedByte(arrayTypeName.componentType);
    }

    public static boolean isChar(TypeName typeName) {
        return typeName.equals(TypeName.CHAR);
    }

    public static boolean isBoxedChar(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.CHAR);
    }

    public static boolean isCharArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isChar(arrayTypeName.componentType);
    }

    public static boolean isBoxedCharArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedChar(arrayTypeName.componentType);
    }

    public static boolean isLong(TypeName typeName) {
        return typeName.equals(TypeName.LONG);
    }

    public static boolean isBoxedLong(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.LONG);
    }

    public static boolean isLongArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isLong(arrayTypeName.componentType);
    }

    public static boolean isBoxedLongArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedLong(arrayTypeName.componentType);
    }

    public static boolean isFloat(TypeName typeName) {
        return typeName.equals(TypeName.FLOAT);
    }

    public static boolean isBoxedFloat(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.FLOAT);
    }


    public static boolean isFloatArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isFloat(arrayTypeName.componentType);
    }

    public static boolean isBoxedFloatArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedFloat(arrayTypeName.componentType);
    }


    public static boolean isPrimitive(TypeName typeName) {
        return typeName.isPrimitive();
    }


    public static boolean isBoxedPrimitive(TypeName typeName) {
        return typeName.isBoxedPrimitive();
    }

    public static boolean isPrimitiveArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isPrimitive(arrayTypeName.componentType);

    }

    public static boolean isBoxedPrimitiveArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedPrimitive(arrayTypeName.componentType);
    }

    public static boolean isString(TypeName typeName) {
        return typeName.equals(TypeNames.STRING);
    }

    public static boolean isStringArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isString(arrayTypeName.componentType);
    }


    public static boolean isSerializable(Elements elements, TypeName typeName) {
        if(typeName.equals(TypeNames.SERIALIZABLE)){
            return true;
        }
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        TypeElement typeElement = elements.getTypeElement(className.toString());
        if (typeElement == null) return false;
        System.out.println("isSerializable ==== " + className.toString() + "========" + typeName.getClass());
        List<TypeMirror> interfaces = (List<TypeMirror>) typeElement.getInterfaces();
        if (interfaces == null || interfaces.size() == 0) return false;
        for (TypeMirror tm : interfaces) {
            if (TypeName.get(tm).equals(TypeNames.SERIALIZABLE)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isParcelable(Elements elements, TypeName typeName) {
        if(typeName.equals(TypeNames.PARCELABLE)){
            return true;
        }
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        TypeElement typeElement = elements.getTypeElement(className.toString());
        if (typeElement == null) return false;
        System.out.println("isSerializable ==== " + className.toString() + "========" + typeName.getClass());
        List<TypeMirror> interfaces = (List<TypeMirror>) typeElement.getInterfaces();
        if (interfaces == null || interfaces.size() == 0) return false;
        for (TypeMirror tm : interfaces) {
            if (TypeName.get(tm).equals(TypeNames.PARCELABLE)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isMap(TypeName typeName) {
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        return className.equals(TypeNames.MAP);
    }

    public static boolean isSet(TypeName typeName) {
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        return className.equals(TypeNames.SET);
    }

    public static boolean isList(TypeName typeName) {
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        return className.equals(TypeNames.LIST);
    }

    public static boolean isArrayList(TypeName typeName) {
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        return className.equals(TypeNames.ARRAY_LIST);
    }

    public static boolean isStringArrayList(TypeName typeName) {
        if (!isList(typeName) && !isArrayList(typeName)) {
            return false;
        }
        ParameterizedTypeName typeName1 = (ParameterizedTypeName) typeName;
        List<TypeName> typeArguments = typeName1.typeArguments;
        if(typeArguments.size() != 1) return false;
        return typeArguments.get(0).equals(TypeNames.STRING);
    }

    public static boolean isIntegerArrayList(TypeName typeName) {
        if (!isList(typeName) && !isArrayList(typeName)) {
            return false;
        }
        ParameterizedTypeName typeName1 = (ParameterizedTypeName) typeName;
        List<TypeName> typeArguments = typeName1.typeArguments;
        if(typeArguments.size() != 1) return false;
        return isBoxedInt(typeArguments.get(0));
    }

    public static boolean isCharSequenceArrayList(TypeName typeName) {
        if (!isList(typeName) && !isArrayList(typeName)) {
            return false;
        }
        ParameterizedTypeName typeName1 = (ParameterizedTypeName) typeName;
        List<TypeName> typeArguments = typeName1.typeArguments;
        if(typeArguments.size() != 1) return false;
        return typeArguments.get(0).equals(TypeNames.CharSequence);
    }


    public static boolean isParcelableArrayList(Elements elements, TypeName typeName) {
        if (!isList(typeName) && !isArrayList(typeName)) {
            return false;
        }
        ParameterizedTypeName typeName1 = (ParameterizedTypeName) typeName;
        List<TypeName> typeArguments = typeName1.typeArguments;
        if(typeArguments.size() != 1) return false;
        return isParcelable(elements,typeArguments.get(0));
    }


}
