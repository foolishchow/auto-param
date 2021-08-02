package me.foolishchow.anrdoid.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import me.foolishchow.android.annotation.Constant;
import me.foolishchow.android.annotation.FragmentParam;
import me.foolishchow.android.annotation.Navigation;
import me.foolishchow.android.annotation.NavigationAction;
import me.foolishchow.anrdoid.processor.base.BaseAnnotationProcessor;

/**
 * Description:
 * Author: foolishchow
 * Date: 13/3/2021 12:35 PM
 */
public class FragmentParamProcessor extends BaseAnnotationProcessor {
    private ArrayList<Element> mElements = new ArrayList<>();

    public FragmentParamProcessor(Messager messager, TypeElement originClass) {
        super(messager, originClass);
    }

    @Override
    public String getTargetClassName(String originClassName) {
        return originClassName + Constant.INTENT_PARAM_SUFFIX;
    }

    public void addField(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)) {
            printError("the modifier of the field must not be private, otherwise it won't work"
                    , element);
            return;
        }
        //if (modifiers.contains(Modifier.PUBLIC)) {
        //    printInfo("the modifier of the field is public, this will expose context info"
        //            , element);
        //}

        mElements.add(element);
    }

    @Override
    public void process(
            Elements elements,
            TypeSpec.Builder builder,
            String packageName,
            String originClassName,
            ClassName originClassType,
            String targetClassName,
            ClassName targetClassType
    ) {

        FieldSpec.Builder field = FieldSpec.builder(
                TypeNames.BUNDLE,
                "mBundle",
                Modifier.PRIVATE, Modifier.FINAL
        );
        field.initializer("new Bundle()");
        builder.addField(field.build());


        if (!isDialog(elements)) {
            Navigation annotation = mOriginClass.getAnnotation(Navigation.class);
            if (annotation != null) {
                NavigationAction[] actions = annotation.actions();
                if (actions != null) {
                    for (NavigationAction action : actions) {
                        addNavigate(builder, action);
                    }

                }
            }
        }
        MethodSpec.Builder getBundle = MethodSpec
                .methodBuilder("getBundle")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeNames.BUNDLE)
                .addStatement("return mBundle");
        ;
        builder.addMethod(getBundle.build());

        MethodSpec.Builder withContext = MethodSpec
                .methodBuilder("with")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(targetClassType)
                .addStatement("return new $T()", targetClassType);
        builder.addMethod(withContext.build());


        MethodSpec.Builder parse = MethodSpec
                .methodBuilder("parse")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(originClassType, "fragment");
        parse.beginControlFlow("if (fragment == null)")
                .addStatement("return")
                .endControlFlow();
        parse.addStatement("$T bundle = fragment.getArguments()", TypeNames.BUNDLE);
        parse.beginControlFlow("if (bundle == null)")
                .addStatement("return")
                .endControlFlow();

        boolean hasCacheToTag = false;
        for (Element element : mElements) {

            String fieldName = element.getSimpleName().toString();
            String keyName = camel2snake(fieldName);

            TypeMirror typeMirror = element.asType();
            TypeName typeName = ParameterizedTypeName.get(typeMirror);


            FragmentParam annotation = element.getAnnotation(FragmentParam.class);
            boolean originName = annotation.originName();
            addFieldKey(builder, fieldName, keyName, originName);
            boolean cacheToTag = annotation.cacheToTag();
            if (cacheToTag) {
                hasCacheToTag = true;
                createField(builder, fieldName, typeName);
            }


            //System.out.println(String.format(
            //        "addElement=>name=%s,type=%s",
            //        element.getSimpleName().toString(),
            //        typeName.toString()
            //));

            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(getSetMethodName(fieldName))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(targetClassType)
                    .addParameter(typeName, "param");

            //method.addComment(format("field %s type %s ", fieldName, typeName.toString()));
            addParamStatement(elements, keyName, typeName, method);
            if (cacheToTag) {
                method.addStatement("this." + fieldName + " = param");
            }
            method.addStatement("return this");
            builder.addMethod(method.build());

            addParse(parse, elements, element, fieldName, keyName, typeName);

        }

        addNormalFragment(builder, originClassType);
        if (hasCacheToTag) {
            addTag(builder, originClassType);
        }
        builder.addMethod(parse.build());

    }

    private void addNavigate(TypeSpec.Builder builder, NavigationAction action) {
        //noinspection ConstantConditions
        if (action.name() == null || action.name().length() == 0) return;

        MethodSpec.Builder navigate = MethodSpec
                .methodBuilder(action.name())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNames.View, "view")
                .returns(void.class);

        navigate.addJavadoc(action.description() + "\n");

        //Navigation
        navigate.addStatement("$T.findNavController(view).navigate(" + action.actionId() + "," +
                "mBundle) ", TypeNames.Navigation);
        builder.addMethod(navigate.build());

    }


    private boolean isDialog(Elements elements) {
        TypeMirror superclass = mOriginClass.getSuperclass();
        if (superclass == null) {
            return false;
        }
        boolean isDialog = false;
        while (superclass != null) {
            if (superclass.toString().equals("androidx.fragment.app.DialogFragment")) {
                isDialog = true;
                superclass = null;
            } else {
                TypeElement typeElement = elements.getTypeElement(superclass.toString());
                superclass = typeElement == null ? null : typeElement.getSuperclass();
            }
        }
        return isDialog;
    }

    private void addTag(
            TypeSpec.Builder builder,
            ClassName originClassType
    ) {
        StringBuilder tagName = new StringBuilder("\"" + originClassType.simpleName() + "\"");
        for (Element element : mElements) {
            FragmentParam annotation = element.getAnnotation(FragmentParam.class);
            boolean cacheToTag = annotation.cacheToTag();
            if (cacheToTag) {
                tagName.append(" + \"-\" + ").append(element.getSimpleName().toString());
            }
        }

        MethodSpec.Builder getBundle = MethodSpec
                .methodBuilder("getTag")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);
        getBundle.addStatement("return " + tagName.toString() + "");
        builder.addMethod(getBundle.build());
    }

    private void addNormalFragment(
            TypeSpec.Builder builder,
            ClassName originClassType
    ) {
        MethodSpec.Builder getBundle = MethodSpec
                .methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(originClassType);
        getBundle.addStatement("$T fragment = null", originClassType)
                .beginControlFlow("try")
                .addStatement("fragment = $T.class.newInstance()", originClassType)
                .addStatement("fragment.setArguments(mBundle)")
                .nextControlFlow("catch ($T e)", TypeNames.Throwable)
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .addStatement("assert fragment != null");
        getBundle.addStatement("return fragment");
        builder.addMethod(getBundle.build());
    }

    private void addParamStatement(
            Elements elements,
            String keyName,
            TypeName typeName,
            MethodSpec.Builder parse
    ) {
        if (TypeUtils.isInt(typeName) || TypeUtils.isBoxedInt(typeName)) {
            parse.addStatement(format("mBundle.putInt(%s,param)", keyName));
        } else if (TypeUtils.isIntArray(typeName)) {
            parse.addStatement(format("mBundle.putIntArray(%s,param)", keyName));
        } else if (TypeUtils.isIntegerArrayList(typeName)) {
            parse.addStatement(format("mBundle.putIntegerArrayList(%s,new $T<>(param))", keyName),
                    TypeNames.ARRAY_LIST);
        } else if (TypeUtils.isByte(typeName) || TypeUtils.isBoxedByte(typeName)) {
            parse.addStatement(format("mBundle.putByte(%s,param)", keyName));
        } else if (TypeUtils.isByteArray(typeName)) {
            parse.addStatement(format("mBundle.putByteArray(%s,param)", keyName));
        } else if (TypeUtils.isBoolean(typeName) || TypeUtils.isBoxedBoolean(typeName)) {
            parse.addStatement(format("mBundle.putBoolean(%s,param)", keyName));
        } else if (TypeUtils.isBooleanArray(typeName)) {
            parse.addStatement(format("mBundle.putBooleanArray(%s,param)", keyName));
        } else if (TypeUtils.isChar(typeName) || TypeUtils.isBoxedChar(typeName)) {
            parse.addStatement(format("mBundle.putChar(%s,param)", keyName));
        } else if (TypeUtils.isCharArray(typeName)) {
            parse.addStatement(format("mBundle.putCharArray(%s,param)", keyName));
        } else if (TypeUtils.isDouble(typeName) || TypeUtils.isBoxedDouble(typeName)) {
            parse.addStatement(format("mBundle.putDouble(%s,param)", keyName));
        } else if (TypeUtils.isDoubleArray(typeName)) {
            parse.addStatement(format("mBundle.putDoubleArray(%s,param)", keyName));
        } else if (TypeUtils.isFloat(typeName) || TypeUtils.isBoxedFloat(typeName)) {
            parse.addStatement(format("mBundle.putFloat(%s,param)", keyName));
        } else if (TypeUtils.isFloatArray(typeName)) {
            parse.addStatement(format("mBundle.putFloatArray(%s,param)", keyName));
        } else if (TypeUtils.isLong(typeName) || TypeUtils.isBoxedLong(typeName)) {
            parse.addStatement(format("mBundle.putLong(%s,param)", keyName));
        } else if (TypeUtils.isLongArray(typeName)) {
            parse.addStatement(format("mBundle.putLongArray(%s,param)", keyName));
        } else if (TypeUtils.isShort(typeName) || TypeUtils.isBoxedShort(typeName)) {
            parse.addStatement(format("mBundle.putShort(%s,param)", keyName));
        } else if (TypeUtils.isShortArray(typeName)) {
            parse.addStatement(format("mBundle.putShortArrayExtra(%s,param)", keyName),
                    TypeNames.ARRAY_LIST);
        } else if (TypeUtils.isString(typeName)) {
            parse.addStatement(format("mBundle.putString(%s,param)", keyName));
        } else if (TypeUtils.isStringArray(typeName)) {
            parse.addStatement(format("mBundle.putStringArray(%s,param)", keyName));
        } else if (TypeUtils.isStringArrayList(typeName)) {
            parse.addStatement(format("mBundle.putStringArrayList(%s,new $T<>(param))", keyName),
                    TypeNames.ARRAY_LIST);
        } else if (TypeUtils.isCharSequence(elements, typeName)) {
            parse.addStatement(format("mBundle.putCharSequence(%s,param)", keyName));
        } else if (TypeUtils.isCharSequenceArray(elements, typeName)) {
            parse.addStatement(format("mBundle.putCharSequenceArray(%s,param)", keyName));
        } else if (TypeUtils.isCharSequenceArrayList(elements, typeName)) {
            parse.addStatement(format("mBundle.putCharSequenceArrayList(%s,param)", keyName));
        } else if (TypeUtils.isParcelable(elements, typeName)) {
            parse.addStatement(format("mBundle.putParcelable(%s,param)", keyName));
        } else if (TypeUtils.isParcelableArray(elements, typeName)) {
            parse.addStatement(format("mBundle.putParcelableArray(%s,param)", keyName));
        } else if (TypeUtils.isParcelableArrayList(elements, typeName)) {
            parse.addStatement(format("mBundle.putParcelableArrayList(%s,new $T(param))",
                    keyName),
                    TypeNames.ARRAY_LIST);
        } else {
            parse.addStatement(format("mBundle.putSerializable(%s,param)", keyName));
        }
        //if (TypeUtils.isStringArrayList(typeName)) {
        //    method.addStatement("mBundle.putStringArrayList(" + keyName + ",new $T<$T>" +
        //            "(param))", TypeNames.ARRAY_LIST, TypeNames.STRING);
        //} else if (TypeUtils.isCharSequenceArrayList(elements, typeName)) {
        //    method.addStatement("mBundle.putCharSequenceArrayList(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.CharSequence);
        //} else if (TypeUtils.isIntegerArrayList(typeName)) {
        //    method.addStatement("mBundle.putCharSequenceArrayList(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.INTEGER);
        //} else if (TypeUtils.isParcelableArrayList(elements, typeName)) {
        //    method.addStatement("mBundle.putParcelableArrayList(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.PARCELABLE);
        //} else if (TypeUtils.isList(typeName)) {
        //    method.addStatement("mBundle.putExtra(" + keyName + ",new $T(param))", TypeNames.ARRAY_LIST);
        //} else if (TypeUtils.isMap(typeName)) {
        //    method.addStatement("mBundle.putExtra(" + keyName + ",new $T(param))", TypeNames.HASH_MAP);
        //} else if (TypeUtils.isSet(typeName)) {
        //    method.addStatement("mBundle.putExtra(" + keyName + ",new $T(param))", TypeNames.HASH_SET);
        //} else {
        //    method.addStatement(format("mBundle.putExtra(%s,param)", keyName));
        //}
    }

    private void addParse(
            MethodSpec.Builder parse,
            Elements elements,
            Element element,
            String fieldName,
            String keyName,
            TypeName typeName
    ) {
        //parse.addComment(format("field %s type %s ", fieldName, typeName.toString()));
        String preffix = format("fragment.%s", fieldName);
        parse.beginControlFlow(format("if (bundle.containsKey(%s))", keyName));

        if (TypeUtils.isInt(typeName) || TypeUtils.isBoxedInt(typeName)) {
            parse.addStatement(format("%s = bundle.getInt(%s,$T.MIN_VALUE)", preffix, keyName), Integer.class);
        } else if (TypeUtils.isIntArray(typeName)) {
            parse.addStatement(format("%s = bundle.getIntArray(%s)", preffix, keyName));
        } else if (TypeUtils.isIntegerArrayList(typeName)) {
            parse.addStatement(format("%s = bundle.getIntegerArrayList(%s)", preffix, keyName));
        } else if (TypeUtils.isByte(typeName) || TypeUtils.isBoxedByte(typeName)) {
            parse.addStatement(format("%s = bundle.getByte(%s,$T.MIN_VALUE)", preffix, keyName), Byte.class);
        } else if (TypeUtils.isByteArray(typeName)) {
            parse.addStatement(format("%s = bundle.getByteArray(%s)", preffix, keyName));
        } else if (TypeUtils.isBoolean(typeName) || TypeUtils.isBoxedBoolean(typeName)) {
            parse.addStatement(format("%s = bundle.getBoolean(%s,false)", preffix, keyName));
        } else if (TypeUtils.isBooleanArray(typeName)) {
            parse.addStatement(format("%s = bundle.getBooleanArray(%s)", preffix, keyName));
        } else if (TypeUtils.isChar(typeName) || TypeUtils.isBoxedChar(typeName)) {
            parse.addStatement(format("%s = bundle.getChar(%s)", preffix, keyName));
        } else if (TypeUtils.isCharArray(typeName)) {
            parse.addStatement(format("%s = bundle.getCharArray(%s)", preffix, keyName));
        } else if (TypeUtils.isDouble(typeName) || TypeUtils.isBoxedDouble(typeName)) {
            parse.addStatement(format("%s = bundle.getDouble(%s,$T.MAX_VALUE)", preffix, keyName), Double.class);
        } else if (TypeUtils.isDoubleArray(typeName)) {
            parse.addStatement(format("%s = bundle.getDoubleArray(%s)", preffix, keyName));
        } else if (TypeUtils.isFloat(typeName) || TypeUtils.isBoxedFloat(typeName)) {
            parse.addStatement(format("%s = bundle.getFloat(%s,$T.MIN_VALUE)", preffix, keyName), Float.class);
        } else if (TypeUtils.isFloatArray(typeName)) {
            parse.addStatement(format("%s = bundle.getFloatArray(%s)", preffix, keyName));
        } else if (TypeUtils.isLong(typeName) || TypeUtils.isBoxedLong(typeName)) {
            parse.addStatement(format("%s = bundle.getLong(%s,$T.MIN_VALUE)", preffix, keyName), Long.class);
        } else if (TypeUtils.isLongArray(typeName)) {
            parse.addStatement(format("%s = bundle.getLongArray(%s)", preffix, keyName));
        } else if (TypeUtils.isShort(typeName) || TypeUtils.isBoxedShort(typeName)) {
            parse.addStatement(format("%s = bundle.getShort(%s,$T.MIN_VALUE)", preffix, keyName), Short.class);
        } else if (TypeUtils.isShortArray(typeName)) {
            parse.addStatement(format("%s = bundle.getShortArrayExtra(%s)", preffix, keyName));
        } else if (TypeUtils.isString(typeName)) {
            parse.addStatement(format("%s = bundle.getString(%s)", preffix, keyName));
        } else if (TypeUtils.isStringArray(typeName)) {
            parse.addStatement(format("%s = bundle.getStringArray(%s)", preffix, keyName));
        } else if (TypeUtils.isStringArrayList(typeName)) {
            parse.addStatement(format("%s = bundle.getStringArrayList(%s)", preffix, keyName));
        } else if (TypeUtils.isCharSequence(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getCharSequence(%s)", preffix, keyName), typeName);
        } else if (TypeUtils.isCharSequenceArray(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getCharSequenceArray(%s)", preffix, keyName), typeName);
        } else if (TypeUtils.isCharSequenceArrayList(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getCharSequenceArrayList(%s)", preffix, keyName), typeName);
        } else if (TypeUtils.isParcelable(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getParcelable(%s)", preffix, keyName), typeName);
        } else if (TypeUtils.isParcelableArray(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getParcelableArray(%s)", preffix, keyName), typeName);
        } else if (TypeUtils.isParcelableArrayList(elements, typeName)) {
            parse.addStatement(format("%s = ($T)bundle.getParcelableArrayList(%s)", preffix, keyName), typeName);
        } else {
            parse.addStatement(format("%s = ($T)bundle.getSerializable(%s)", preffix, keyName), typeName);
        }

        parse.endControlFlow();
    }

    private void createField(TypeSpec.Builder builder, String fieldName, TypeName typeName) {

        FieldSpec.Builder field = FieldSpec.builder(
                typeName,
                fieldName,
                Modifier.PRIVATE
        );
        builder.addField(field.build());
    }

    private void addFieldKey(TypeSpec.Builder builder, String fieldName, String keyName, boolean originName) {
        FieldSpec.Builder field = FieldSpec.builder(
                TypeNames.STRING,
                keyName,
                Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
        );
        field.initializer(wrapString(originName ? fieldName : escapeString(fieldName)));
        builder.addField(field.build());
    }

}
