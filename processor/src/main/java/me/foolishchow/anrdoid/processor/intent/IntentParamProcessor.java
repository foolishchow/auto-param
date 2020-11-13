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
        parse.addStatement("$T intent = activity.getIntent();", TypeNames.INTENT);
        parse.beginControlFlow("if(intent == null)")
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
            FieldSpec.Builder field = FieldSpec.builder(
                    TypeNames.STRING,
                    keyName,
                    Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
            );
            field.initializer(wrapString(escapeString(fieldName)));
            builder.addField(field.build());

            TypeMirror typeMirror = element.asType();
            TypeName typeName = ParameterizedTypeName.get(typeMirror);

            System.out.println(String.format(
                    "addElement=>name=%s,type=%s",
                    element.getSimpleName().toString(),
                    typeName.toString()
            ));

            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(getSetMethodName(fieldName))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(targetClassType)
                    .addParameter(typeName, "param");

            if (IntentTypeUtils.isStringArrayList(typeName)) {
                method.addStatement("mIntent.putStringArrayListExtra(" + keyName + ",new $T<$T>" +
                        "(param))", TypeNames.ARRAY_LIST, TypeNames.STRING);
            } else if (IntentTypeUtils.isCharSequenceArrayList(typeName)) {
                method.addStatement("mIntent.putCharSequenceArrayListExtra(" + keyName + ",new $T<$T>" +
                        "(param))", TypeNames.ARRAY_LIST, TypeNames.CharSequence);
            } else if (IntentTypeUtils.isIntegerArrayList(typeName)) {
                method.addStatement("mIntent.putCharSequenceArrayListExtra(" + keyName + ",new $T<$T>" +
                        "(param))", TypeNames.ARRAY_LIST, TypeNames.INTEGER);
            } else if (IntentTypeUtils.isParcelableArrayList(elements, typeName)) {
                method.addStatement("mIntent.putParcelableArrayListExtra(" + keyName + ",new " +
                        "$T<$T>(param))", TypeNames.ARRAY_LIST, TypeNames.PARCELABLE);
            } else if (IntentTypeUtils.isList(typeName)) {
                method.addStatement("mIntent.putExtra(" + keyName + ",new " +
                        "$T(param))", TypeNames.ARRAY_LIST);
            } else if (IntentTypeUtils.isMap(typeName)) {
                method.addStatement("mIntent.putExtra(" + keyName + ",new " +
                        "$T(param))", TypeNames.HASH_MAP);
            } else if (IntentTypeUtils.isSet(typeName)) {
                method.addStatement("mIntent.putExtra(" + keyName + ",new " +
                        "$T(param))", TypeNames.HASH_SET);
            } else {
                method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
            }


            //
            //if (IntentTypeUtils.isPrimitive(typeName) ||
            //        IntentTypeUtils.isBoxedPrimitive(typeName) ||
            //        IntentTypeUtils.isPrimitiveArray(typeName) ||
            //        IntentTypeUtils.isBoxedPrimitiveArray(typeName) ||
            //        IntentTypeUtils.isString(typeName) ||
            //        IntentTypeUtils.isStringArray(typeName)
            //) {
            //    method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
            //} else
            //if (IntentTypeUtils.isPrimitive(typeName)) {
            //    method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
            //} else if (IntentTypeUtils.isPrimitiveArray(typeName)) {
            //    method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
            //} else if (IntentTypeUtils.isPrimitiveArray(typeName)) {
            //    method.addStatement(format("mIntent.putExtra(%s,param)", keyName));
            //}

            method.addStatement("return this");


            //parse.addStatement(
            //        format(
            //                "activity.%s = intent.getIntExtra(%s,-1)",
            //                fieldName, keyName));
            builder.addMethod(method.build());

            addParse(parse, elements, element, fieldName, keyName, typeName);
            //typeName.isPrimitive()
        }

        builder.addMethod(parse.build());

    }


    private void addParse(
            MethodSpec.Builder parse,
            Elements elements,
            Element element,
            String fieldName,
            String keyName,
            TypeName typeName
    ) {
        String preffix = format("activity.%s = intent",fieldName);
        parse.beginControlFlow(format("if(intent.hasExtra(%s))", keyName));
        if (IntentTypeUtils.isInt(typeName) || IntentTypeUtils.isBoxedInt(typeName)) {
            parse.addStatement(format("%s.getIntExtra(%s,-1)", preffix,
                    keyName));
        } else if (IntentTypeUtils.isByte(typeName) || IntentTypeUtils.isBoxedByte(typeName)) {
            parse.addStatement(format("%s.getByteExtra(%s,$T.MIN_VALUE)",
                    preffix,
                    keyName), Byte.class);
        } else if (IntentTypeUtils.isBoolean(typeName) || IntentTypeUtils.isBoxedBoolean(typeName)) {
            parse.addStatement(format("%s.getBooleanExtra(%s,false)",
                    preffix, keyName));
        }else if (IntentTypeUtils.isLong(typeName) || IntentTypeUtils.isBoxedLong(typeName)) {
            parse.addStatement(format("%s.getLongExtra(%s,1L)",
                    preffix, keyName));
        }else if (IntentTypeUtils.isFloat(typeName) || IntentTypeUtils.isBoxedFloat(typeName)) {
            parse.addStatement(format("%s.getBooleanExtra(%s,-1f)",
                    preffix, keyName));
        }
        parse.endControlFlow();
    }

}
