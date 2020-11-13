package me.foolishchow.anrdoid.processor.intent;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
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
import me.foolishchow.anrdoid.processor.TypeUtil;
import me.foolishchow.anrdoid.processor.base.BaseAnnotationProcessor;
import me.foolishchow.anrdoid.processor.base.TypeNames;

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

        System.out.println(String.format(
                "addElement=>name=%s,type=%s",
                element.getSimpleName().toString(),
                element.asType().toString()
        ));

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
            TypeName originClassType,
            String targetClassName,
            TypeName targetClassType
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
                .addStatement("$T var = new $T();", targetClassType, targetClassType)
                .addStatement("var.setIntent(new $T(context,$T.class))", TypeNames.INTENT,
                        originClassType)
                .addStatement("return var");
        builder.addMethod(withContext.build());


        for (Element element : mElements) {

            String fieldName = element.getSimpleName().toString();
            String keyName = camel2snake(fieldName);
            FieldSpec.Builder field = FieldSpec.builder(
                    TypeNames.String,
                    keyName,
                    Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
            );
            field.initializer(wrapString(escapeString(fieldName)));
            builder.addField(field.build());

            TypeMirror typeMirror = element.asType();
            TypeName typeName = TypeName.get(typeMirror);


            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(getSetMethodName(fieldName))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(targetClassType)
                    .addParameter(typeName, "param");

            if (typeName.isPrimitive() || typeName.isBoxedPrimitive()) {
                method.addStatement(format("mIntent.putExtra(%s,param)", camel2snake(fieldName)));
            } else {
                if (typeName instanceof ArrayTypeName) {
                    ArrayTypeName arrayTypeName = (ArrayTypeName) typeName;
                    if (
                            arrayTypeName.componentType.isPrimitive()
                                    || arrayTypeName.componentType.isBoxedPrimitive()
                    ) {
                        method.addStatement(format("mIntent.putExtra(%s,param)", camel2snake(fieldName)));
                    }
                }
            }

            method.addStatement("return this");


            //parse.addStatement(
            //        format(
            //                "activity.%s = intent.getIntExtra(%s,-1)",
            //                fieldName, keyName));
            builder.addMethod(method.build());


            //typeName.isPrimitive()
        }

        builder.addMethod(parse.build());

    }


}
