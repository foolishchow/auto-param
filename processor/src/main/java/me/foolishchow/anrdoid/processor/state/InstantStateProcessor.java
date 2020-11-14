package me.foolishchow.anrdoid.processor.state;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import me.foolishchow.android.annotation.Constant;
import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.anrdoid.processor.base.BaseAnnotationProcessor;
import me.foolishchow.anrdoid.processor.TypeUtils;
import me.foolishchow.anrdoid.processor.TypeNames;


public class InstantStateProcessor extends BaseAnnotationProcessor {
    //普通状态
    private List<Element> mCommonElements = new ArrayList<>();
    //持久状态
    private List<Element> mPersistElement = new ArrayList<>();


    private static final ClassName HELPER_INTERFACE = ClassName.get(
            "me.foolishchow.android.utils.helpers",
            "IInstanceStateHelper");

    private TypeName getInterfaceType(TypeName name) {
        return ParameterizedTypeName.get(HELPER_INTERFACE, name);
    }

    public InstantStateProcessor(Messager messager, TypeElement encloseElement) {
        super(messager, encloseElement);
    }

    @Override
    public String getTargetClassName(String originClassName) {
        return originClassName + Constant.INSTANT_STATE_SUFFIX;
    }


    public void addField(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)) {
            printError("the modifier of the field must not be private, otherwise it won't work", element);
            return;
        }
        if (modifiers.contains(Modifier.PUBLIC)) {
            printInfo("the modifier of the field is public, this will expose context info", element);
        }
        if (element.getAnnotation(InstanceState.class).persist()) {
            TypeMirror typeMirror = element.asType();
            TypeName typeName = ParameterizedTypeName.get(typeMirror);
            if (
                    TypeUtils.isString(typeName) ||
                            TypeUtils.isStringArray(typeName) ||
                            TypeUtils.isPrimitive(typeName) ||
                            TypeUtils.isBoxedPrimitive(typeName) ||
                            TypeUtils.isPrimitiveArray(typeName) ||
                            TypeUtils.isBoxedPrimitiveArray(typeName)
            ) {
                mPersistElement.add(element);
            } else {
                printError("persist only can apply to String,Primitive,BoxedPrimitive,StringArray,PrimitiveArray,BoxedPrimitiveArray ",
                        element);
            }
        } else {
            mCommonElements.add(element);
        }
    }

    public static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static final ClassName PERSISTABLE_BUNDLE = ClassName.get("android.os", "PersistableBundle");
    private static final String saveMethodName = "saveInstanceState";

    private static final String restoreMethodName = "restoreInstanceState";


    @Override
    public void process(Elements elements,
                        TypeSpec.Builder builder,
                        String packageName,
                        String originClassName,
                        ClassName originClassType,
                        String targetClassName,
                        ClassName targetClassType
    ) {
        builder.addSuperinterface(getInterfaceType(originClassType));
        MethodSpec.Builder saveMethod = MethodSpec
                .methodBuilder(saveMethodName)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(BUNDLE, "outState")
                .addParameter(PERSISTABLE_BUNDLE, "outPersistentState")
                .addParameter(originClassType, "instance");


        MethodSpec.Builder restoreMethod = MethodSpec
                .methodBuilder(restoreMethodName)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(BUNDLE, "savedState")
                .addParameter(PERSISTABLE_BUNDLE, "savedPersistentState")
                .addParameter(originClassType, "instance");

        if (mCommonElements.size() > 0) {
            restoreMethod.beginControlFlow("if(savedState != null)");
            saveMethod.beginControlFlow("if(outState != null)");
            for (Element element : mCommonElements) {
                String fieldName = element.getSimpleName().toString();
                String fieldKey = camel2snake(fieldName);
                TypeMirror typeMirror = element.asType();
                TypeName typeName = ParameterizedTypeName.get(typeMirror);
                String androidTypeName = getTypeName(elements, typeName);


                createFieldKey(builder, fieldName, fieldKey);
                restoreMethod.beginControlFlow(format("if (savedState.containsKey(%s))",fieldKey));
                if (typeName.isPrimitive()) {
                    restoreMethod.addStatement(format("instance.%s = savedState.get%s(%s)", fieldName,androidTypeName, fieldKey));
                    saveMethod.addStatement(format("outState.put%s(%s,instance.%s)", androidTypeName, fieldKey, fieldName));
                } else {
                    saveMethod.beginControlFlow(format("if (instance.%s != null)", fieldName))
                            .addStatement(format("outState.put%s(%s,instance.%s)", androidTypeName, fieldKey, fieldName))
                            .endControlFlow();
                    restoreMethod.addStatement(format("instance.%s = ($T)savedState.get%s(%s)", fieldName,androidTypeName, fieldKey),
                            typeName);
                }
                restoreMethod.endControlFlow();



            }
            restoreMethod.endControlFlow();
            saveMethod.endControlFlow();
        }
        if (mPersistElement.size() > 0) {
            restoreMethod.beginControlFlow("if(savedPersistentState != null)");
            saveMethod.beginControlFlow("if(outPersistentState != null)");
            for (Element element : mPersistElement) {
                String fieldName = element.getSimpleName().toString();
                String fieldKey = camel2snake(fieldName);
                TypeMirror typeMirror = element.asType();
                TypeName typeName = ParameterizedTypeName.get(typeMirror);
                String androidTypeName = getTypeName(elements, typeName);

                createFieldKey(builder, fieldName, fieldKey);
                restoreMethod.beginControlFlow(format("if (savedPersistentState.containsKey(%s))",fieldKey));
                if (typeName.isPrimitive()) {
                    saveMethod.addStatement(format("outPersistentState.put%s(%s,instance.%s)", androidTypeName, fieldKey, fieldName));
                } else {
                    saveMethod.beginControlFlow(format("if (instance.%s != null)", fieldName))
                            .addStatement(format("outPersistentState.put%s(%s,instance.%s)", androidTypeName, fieldKey, fieldName))
                            .endControlFlow();
                }
                restoreMethod.addStatement(format("instance.%s = savedPersistentState.get%s(%s)", fieldName,androidTypeName, fieldKey));
                restoreMethod.endControlFlow();
            }
            restoreMethod.endControlFlow();
            saveMethod.endControlFlow();
        }


        builder.addMethod(saveMethod.build());
        builder.addMethod(restoreMethod.build());
    }


    private String getTypeName(Elements elements, TypeName typeName) {

        if (TypeUtils.isByte(typeName) || TypeUtils.isBoxedByte(typeName)) {
            return "Byte";
        }
        if (TypeUtils.isByteArray(typeName)) {
            return "ByteArray";
        }
        if (TypeUtils.isChar(typeName) || TypeUtils.isBoxedChar(typeName)) {
            return "Char";
        }
        if (TypeUtils.isCharArray(typeName)) {
            return "CharArray";
        }
        if (TypeUtils.isDouble(typeName) || TypeUtils.isBoxedDouble(typeName)) {
            return "Double";
        }
        if (TypeUtils.isCharArray(typeName)) {
            return "DoubleArray";
        }
        if (TypeUtils.isFloat(typeName) || TypeUtils.isBoxedFloat(typeName)) {
            return "Float";
        }
        if (TypeUtils.isCharArray(typeName)) {
            return "FloatArray";
        }
        if (TypeUtils.isInt(typeName) || TypeUtils.isBoxedInt(typeName)) {
            return "Int";
        }
        if (TypeUtils.isIntArray(typeName)) {
            return "IntArray";
        }
        if (TypeUtils.isLong(typeName) || TypeUtils.isBoxedLong(typeName)) {
            return "Long";
        }
        if (TypeUtils.isLongArray(typeName)) {
            return "LongArray";
        }
        if (TypeUtils.isShort(typeName) || TypeUtils.isBoxedShort(typeName)) {
            return "Short";
        }
        if (TypeUtils.isShortArray(typeName)) {
            return "ShortArray";
        }
        if (TypeUtils.isString(typeName)) {
            return "String";
        }
        if (TypeUtils.isStringArray(typeName)) {
            return "StringArray";
        }
        if (TypeUtils.isStringArrayList(typeName)) {
            return "StringArrayList";
        }
        if (TypeUtils.isCharSequence(elements, typeName)) {
            return "CharSequence";
        }
        if (TypeUtils.isCharSequenceArray(elements, typeName)) {
            return "CharSequenceArray";
        }
        if (TypeUtils.isCharSequenceArrayList(elements, typeName)) {
            return "CharSequenceArrayList";
        }
        if (TypeUtils.isSize(typeName)) {
            return "Size";
        }
        if (TypeUtils.isSizeF(typeName)) {
            return "SizeF";
        }
        if (TypeUtils.isParcelable(elements, typeName)) {
            return "Parcelable";
        }
        if (TypeUtils.isParcelableArray(elements, typeName)) {
            return "ParcelableArray";
        }
        if (TypeUtils.isParcelableArrayList(elements, typeName)) {
            return "ParcelableArrayList";
        }
        if (TypeUtils.isSerializable(elements, typeName)) {
            return "Serializable";
        }
        return "";
    }

    private void createFieldKey(TypeSpec.Builder builder, String fieldName, String fieldKey) {
        FieldSpec.Builder field = FieldSpec.builder(
                TypeNames.STRING,
                fieldKey,
                Modifier.PRIVATE, Modifier.FINAL
        );
        field.initializer(wrapString(escapeString(fieldName)));
        builder.addField(field.build());
    }


}
