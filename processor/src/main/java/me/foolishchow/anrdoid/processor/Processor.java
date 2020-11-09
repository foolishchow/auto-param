package me.foolishchow.anrdoid.processor;

import me.foolishchow.android.annotation.InstanceState;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class Processor extends AbstractProcessor {

    private Messager messager;
    //存储添加了注解的Activity
    private Map<String, HelperClass> mHelperClassMap = new HashMap<>();
    //可以处理相关Element（包括ExecutableElement, PackageElement, TypeElement, TypeParameterElement, VariableElement）
    private Elements elementUtils;
    //用来创建新源、类或辅助文件的 Filer。
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        //filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        //elements = processingEnvironment.getElementUtils();
        //activitiesWithPackage = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /**
         * 1- Find all annotated element
         */
        for (Element element : roundEnvironment.getElementsAnnotatedWith(InstanceState.class)) {
            getHelperClass(element);
            //System.out.println("============="+element.getSimpleName());
            //if (element.getKind() != ElementKind.CLASS) {
            //    messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
            //    return true;
            //}
            //
            //TypeElement typeElement = (TypeElement) element;
            //activitiesWithPackage.put(
            //        typeElement.getSimpleName().toString(),
            //        elements.getPackageOf(typeElement).getQualifiedName().toString());
        }

        //遍历生成java代码
        for(HelperClass helperClass : mHelperClassMap.values()){
            try {

                //获取helperClass，调用其方法直接生成java代码
                JavaFile javaFile = helperClass.generateCode();
                if(javaFile != null){
                    javaFile.writeTo(filer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        ///**
        // * 2- Generate a class
        // */
        //TypeSpec.Builder navigatorClass = TypeSpec
        //        .classBuilder("Navigator")
        //        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        //
        //for (Map.Entry<String, String> element : activitiesWithPackage.entrySet()) {
        //    String activityName = element.getKey();
        //    String packageName = element.getValue();
        //    ClassName activityClass = ClassName.get(packageName, activityName);
        //    MethodSpec intentMethod = MethodSpec
        //            .methodBuilder(METHOD_PREFIX + activityName)
        //            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        //            .returns(void.class)
        //            .addParameter(classContext, "context")
        //            .addStatement("$L.startActivity(new $T($L, $L))", "context", classIntent, "context", activityClass + ".class")
        //            .build();
        //    navigatorClass.addMethod(intentMethod);
        //}
        //
        //
        ///**
        // * 3- Write generated class to a file
        // */
        //JavaFile.builder("com.annotationsample", navigatorClass.build()).build().writeTo(filer);


        return true;
    }


    private HelperClass getHelperClass(Element element) {
        TypeElement encloseElement = (TypeElement) element.getEnclosingElement();
        //所在类的完整类名
        String fullClassName = encloseElement.getQualifiedName().toString();
        //通过所在类的类名获取HelperClass类，HelperClass是用于自动生成代码的对象
        HelperClass annotatedClass = mHelperClassMap.get(fullClassName);
        if (annotatedClass == null) {
            annotatedClass = new HelperClass(encloseElement, elementUtils, messager);
            mHelperClassMap.put(fullClassName, annotatedClass);
        }
        //HelperSavedValues是被@NeedSave标记的字段，然后把这些字段添加到对应的map中
        //也就是map的key为需要生成的类，value为生成这个类的方法对象，其中包括了所有的需要保存的元素
        HelperSavedValues values = new HelperSavedValues(element);
        annotatedClass.addField(values);//添加当前类中的 所被注解标记的元素
        return annotatedClass;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(InstanceState.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}