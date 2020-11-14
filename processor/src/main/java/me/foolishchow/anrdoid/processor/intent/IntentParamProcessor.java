package me.foolishchow.anrdoid.processor.intent;

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
import me.foolishchow.anrdoid.processor.base.BaseAnnotationProcessor;

public class IntentParamProcessor extends BaseAnnotationProcessor {

    private static ClassName TYPE_PARENT = ClassName.get("me.foolishchow.android.utils",
            "IntentBuilder");

    private ArrayList<Element> mElements = new ArrayList<>();

    public IntentParamProcessor(Messager messager, TypeElement encloseElement) {
        super(messager, encloseElement);
    }

    public void addElement(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)) {
            printError("the modifier of the field must not be private, otherwise it won't work"
                    , element);
            return;
        }
        if (modifiers.contains(Modifier.PUBLIC)) {
            printInfo("the modifier of the field is public, this will expose context info"
                    , element);
        }

        mElements.add(element);
    }

    @Override
    public String getTargetClassName(String originClassName) {
        return originClassName + Constant.INTENT_PARAM_SUFFIX;
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
        builder.superclass(TYPE_PARENT);


        MethodSpec.Builder parse = MethodSpec
                .methodBuilder("parse")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(originClassType, "activity");
        parse.beginControlFlow("if(activity == null || activity.isFinishing() || activity" +
                ".isDestroyed())")
                .addStatement("return")
                .endControlFlow();
        parse.addStatement("$T intent = activity.getIntent()", TypeNames.INTENT);
        parse.beginControlFlow("if (intent == null)")
                .addStatement("return")
                .endControlFlow();


        MethodSpec.Builder withContext = MethodSpec
                .methodBuilder("with")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(targetClassType)
                .addParameter(TypeNames.CONTEXT, "context")
                .addStatement("$T var = new $T()", targetClassType, targetClassType)
                .addStatement("var.setIntent(new $T(context,$T.class))", TypeNames.INTENT,
                        originClassType)
                .addStatement("return var");
        builder.addMethod(withContext.build());


        for (Element element : mElements) {

            String fieldName = element.getSimpleName().toString();
            String keyName = camel2snake(fieldName);
            addFieldKey(builder, fieldName, keyName);

            TypeMirror typeMirror = element.asType();
            TypeName typeName = ParameterizedTypeName.get(typeMirror);

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
            method.addStatement("return this");
            builder.addMethod(method.build());

            addParse(parse, elements, element, fieldName, keyName, typeName);

        }

        builder.addMethod(parse.build());

    }

    private void addFieldKey(TypeSpec.Builder builder, String fieldName, String keyName) {
        FieldSpec.Builder field = FieldSpec.builder(
                TypeNames.STRING,
                keyName,
                Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
        );
        field.initializer(wrapString(escapeString(fieldName)));
        builder.addField(field.build());
    }

    private void addParamStatement(Elements elements, String keyName, TypeName typeName, MethodSpec.Builder method) {
        if (IntentTypeUtils.isStringArrayList(typeName)) {
            method.addStatement("mIntent.putStringArrayListExtra(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.STRING);
        } else if (IntentTypeUtils.isCharSequenceArrayList(elements,typeName)) {
            method.addStatement("mIntent.putCharSequenceArrayListExtra(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.CharSequence);
        } else if (IntentTypeUtils.isIntegerArrayList(typeName)) {
            method.addStatement("mIntent.putCharSequenceArrayListExtra(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.INTEGER);
        } else if (IntentTypeUtils.isParcelableArrayList(elements, typeName)) {
            method.addStatement("mIntent.putParcelableArrayListExtra(" + keyName + ",new $T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.PARCELABLE);
        } else if (IntentTypeUtils.isList(typeName)) {
            method.addStatement("mIntent.putExtra(" + keyName + ",new $T(param))", TypeNames.ARRAY_LIST);
        } else if (IntentTypeUtils.isMap(typeName)) {
            method.addStatement("mIntent.putExtra(" + keyName + ",new $T(param))", TypeNames.HASH_MAP);
        } else if (IntentTypeUtils.isSet(typeName)) {
            method.addStatement("mIntent.putExtra(" + keyName + ",new $T(param))", TypeNames.HASH_SET);
        } else {
            method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
        }
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
        String preffix = format("activity.%s", fieldName);
        parse.beginControlFlow(format("if (intent.hasExtra(%s))", keyName));

        if (IntentTypeUtils.isInt(typeName) || IntentTypeUtils.isBoxedInt(typeName)) {
            parse.addStatement(format("%s = intent.getIntExtra(%s,$T.MIN_VALUE)", preffix, keyName), Integer.class);
        } else if (IntentTypeUtils.isIntArray(typeName)) {
            parse.addStatement(format("%s = intent.getIntArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isIntegerArrayList(typeName)) {
            parse.addStatement(format("%s = intent.getIntegerArrayListExtra(%s)", preffix, keyName));
        }else if (IntentTypeUtils.isByte(typeName) || IntentTypeUtils.isBoxedByte(typeName)) {
            parse.addStatement(format("%s = intent.getByteExtra(%s,$T.MIN_VALUE)", preffix, keyName), Byte.class);
        } else if (IntentTypeUtils.isByteArray(typeName)) {
            parse.addStatement(format("%s = intent.getByteArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isBoolean(typeName) || IntentTypeUtils.isBoxedBoolean(typeName)) {
            parse.addStatement(format("%s = intent.getBooleanExtra(%s,false)", preffix, keyName));
        } else if (IntentTypeUtils.isBooleanArray(typeName)) {
            parse.addStatement(format("%s = intent.getBooleanArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isChar(typeName) || IntentTypeUtils.isBoxedChar(typeName)) {
            parse.addStatement(format("%s = intent.getCharExtra(%s,$T.MIN_CODE_POINT)", preffix, keyName), Character.class);
        } else if (IntentTypeUtils.isCharArray(typeName)) {
            parse.addStatement(format("%s = intent.getCharArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isDouble(typeName) || IntentTypeUtils.isBoxedDouble(typeName)) {
            parse.addStatement(format("%s = intent.getDoubleExtra(%s,$T.MAX_VALUE)", preffix, keyName), Double.class);
        } else if (IntentTypeUtils.isDoubleArray(typeName)) {
            parse.addStatement(format("%s = intent.getDoubleArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isFloat(typeName) || IntentTypeUtils.isBoxedFloat(typeName)) {
            parse.addStatement(format("%s = intent.getFloatExtra(%s,$T.MIN_VALUE)", preffix, keyName), Float.class);
        } else if (IntentTypeUtils.isFloatArray(typeName)) {
            parse.addStatement(format("%s = intent.getFloatArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isLong(typeName) || IntentTypeUtils.isBoxedLong(typeName)) {
            parse.addStatement(format("%s = intent.getLongExtra(%s,$T.MIN_VALUE)", preffix, keyName), Long.class);
        } else if (IntentTypeUtils.isLongArray(typeName)) {
            parse.addStatement(format("%s = intent.getLongArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isShort(typeName) || IntentTypeUtils.isBoxedShort(typeName)) {
            parse.addStatement(format("%s = intent.getShortExtra(%s,$T.MIN_VALUE)", preffix, keyName), Short.class);
        } else if (IntentTypeUtils.isShortArray(typeName)) {
            parse.addStatement(format("%s = intent.getShortArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isString(typeName)) {
            parse.addStatement(format("%s = intent.getStringExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isStringArray(typeName)) {
            parse.addStatement(format("%s = intent.getStringArrayExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isStringArrayList(typeName)) {
            parse.addStatement(format("%s = intent.getStringArrayListExtra(%s)", preffix, keyName));
        } else if (IntentTypeUtils.isCharSequence(elements, typeName)) {
            parse.addStatement(format("%s = ($T)intent.getCharSequenceExtra(%s)", preffix, keyName), typeName);
        } else if (IntentTypeUtils.isCharSequenceArray(elements, typeName)) {
            parse.addStatement(format("%s = ($T)intent.getCharSequenceArrayExtra(%s)", preffix, keyName), typeName);
        } else if (IntentTypeUtils.isCharSequenceArrayList(elements,typeName)) {
            parse.addStatement(format("%s = ($T)intent.getCharSequenceArrayListExtra(%s)", preffix, keyName), typeName);
        } else if (IntentTypeUtils.isParcelable(elements, typeName)) {
            parse.addStatement(format("%s = ($T)intent.getParcelableExtra(%s)", preffix, keyName), typeName);
        }else if (IntentTypeUtils.isParcelableArray(elements, typeName)) {
            parse.addStatement(format("%s = ($T)intent.getParcelableArrayExtra(%s)", preffix, keyName), typeName);
        }else if (IntentTypeUtils.isParcelableArrayList(elements, typeName)) {
            parse.addStatement(format("%s = ($T)intent.getParcelableArrayListExtra(%s)", preffix, keyName), typeName);
        }else{
            parse.addStatement(format("%s = ($T)intent.getSerializableExtra(%s)", preffix, keyName), typeName);
        }

        parse.endControlFlow();
    }

}
