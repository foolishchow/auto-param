package me.foolishchow.anrdoid.processor.state;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import me.foolishchow.android.annotation.Constant;
import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.anrdoid.processor.base.BaseAnnotationProcessor;
import me.foolishchow.anrdoid.processor.base.TypeNames;


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
            mPersistElement.add(element);
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
                        TypeName originClassType,
                        String targetClassName,
                        TypeName targetClassType
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

        if(mCommonElements.size() > 0){
            restoreMethod.beginControlFlow("if(savedState != null)");
            saveMethod.beginControlFlow("if(outState != null)");
            for (Element element : mCommonElements) {
                String fieldName = element.getSimpleName().toString();
                FieldSpec.Builder field = FieldSpec.builder(
                        TypeNames.String,
                        camel2snake(fieldName),
                        Modifier.PRIVATE, Modifier.FINAL
                );
                field.initializer(wrapString(escapeString(fieldName)));
                builder.addField(field.build());

            }
            restoreMethod.endControlFlow();
            saveMethod.endControlFlow();
        }
        if(mPersistElement.size() > 0){
            for (Element element : mPersistElement) {
                String fieldName = element.getSimpleName().toString();
                FieldSpec.Builder field = FieldSpec.builder(
                        TypeNames.String,
                        camel2snake(fieldName),
                        Modifier.PRIVATE, Modifier.FINAL
                );

                String name = escapeString(fieldName);
                field.initializer(wrapString(name));
                builder.addField(field.build());

            }
        }


        builder.addMethod(saveMethod.build());
        builder.addMethod(restoreMethod.build());


    }


}
