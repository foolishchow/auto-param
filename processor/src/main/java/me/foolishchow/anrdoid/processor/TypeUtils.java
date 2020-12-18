package me.foolishchow.anrdoid.processor;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/11/2020 1:35 PM
 */
public class TypeUtils {

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
    public static boolean isBooleanArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoolean(arrayTypeName.componentType);
    }

    public static boolean isBoxedBooleanArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedBoolean(arrayTypeName.componentType);
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


    public static boolean isDouble(TypeName typeName) {
        return typeName.equals(TypeName.DOUBLE);
    }

    public static boolean isBoxedDouble(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.DOUBLE);
    }


    public static boolean isDoubleArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isDouble(arrayTypeName.componentType);
    }

    public static boolean isBoxedDoubleArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedDouble(arrayTypeName.componentType);
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


    public static boolean isShort(TypeName typeName) {
        return typeName.equals(TypeName.SHORT);
    }

    public static boolean isBoxedShort(TypeName typeName) {
        return typeName.isBoxedPrimitive() && typeName.unbox().equals(TypeName.SHORT);
    }
    public static boolean isShortArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isShort(arrayTypeName.componentType);
    }

    public static boolean isBoxedShortArray(TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isBoxedShort(arrayTypeName.componentType);
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

    public static boolean isCharSequence(Elements elements, TypeName typeName) {
        if(typeName.equals(TypeNames.CharSequence)){
            return true;
        }
        ClassName className = null;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType;
        } else if (typeName instanceof ClassName) {
            className = (ClassName) typeName;
        }
        if (className == null) return false;
        return implementInterface(elements,className,TypeNames.CharSequence);
    }

    public static boolean isCharSequenceArray(Elements elements,TypeName typeName) {
        if (!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isCharSequence(elements,arrayTypeName.componentType);
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
        return implementInterface(elements,className,TypeNames.SERIALIZABLE);
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
        return implementInterface(elements,className,TypeNames.PARCELABLE);
    }
    public static boolean isParcelableArray(Elements elements, TypeName typeName) {
        if(!isArray(typeName)) return false;
        ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
        return isParcelable(elements,arrayTypeName.componentType);
    }


    public static boolean implementInterface(Elements elements, ClassName className, ClassName interfaceClassName){
        TypeElement typeElement = elements.getTypeElement(className.toString());
        if (typeElement == null) return false;
        List<TypeMirror> interfaces = (List<TypeMirror>) typeElement.getInterfaces();
        if (interfaces == null || interfaces.size() == 0) return false;
        for (TypeMirror tm : interfaces) {
            if (TypeName.get(tm).equals(interfaceClassName)) {
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

    public static boolean isCharSequenceArrayList(Elements elements,TypeName typeName) {
        if (!isList(typeName) && !isArrayList(typeName)) {
            return false;
        }
        ParameterizedTypeName typeName1 = (ParameterizedTypeName) typeName;
        List<TypeName> typeArguments = typeName1.typeArguments;
        if(typeArguments.size() != 1) return false;
        return isCharSequence(elements,typeName);
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

    public static boolean isSize(TypeName typeName) {
        return typeName.equals(TypeNames.SIZE);
    }
    public static boolean isSizeF(TypeName typeName) {
        return typeName.equals(TypeNames.SIZEF);
    }
}
