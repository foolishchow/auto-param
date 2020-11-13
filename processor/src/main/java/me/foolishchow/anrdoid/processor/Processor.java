package me.foolishchow.anrdoid.processor;

import me.foolishchow.android.annotation.InstanceState;
import me.foolishchow.android.annotation.IntentParam;
import me.foolishchow.anrdoid.processor.intent.IntentParamProcessor;
import me.foolishchow.anrdoid.processor.state.HelperClass;
import me.foolishchow.anrdoid.processor.state.InstantStateProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class Processor extends AbstractProcessor {

    private Messager messager;
    //存储添加了注解的Activity

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
        System.out.println("=============process");
        Map<String, InstantStateProcessor> mInstantStateMap = new HashMap<>();
        Map<String, IntentParamProcessor> mIntentParamMap = new HashMap<>();
        /*
         * 1- Find all annotated element
         */
        for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentParam.class)) {

            TypeElement encloseElement = (TypeElement) element.getEnclosingElement();
            //所在类的完整类名
            String fullClassName = encloseElement.getQualifiedName().toString();
            IntentParamProcessor processor = mIntentParamMap.get(fullClassName);
            if(processor == null){
                processor = new IntentParamProcessor(messager,encloseElement);
                mIntentParamMap.put(fullClassName, processor);
            }
            processor.addElement(element);
        }

        for(IntentParamProcessor processor : mIntentParamMap.values()){
            processor.generate(filer,elementUtils);
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(InstanceState.class)) {
            TypeElement encloseElement = (TypeElement) element.getEnclosingElement();
            //所在类的完整类名
            String fullClassName = encloseElement.getQualifiedName().toString();
            //通过所在类的类名获取HelperClass类，HelperClass是用于自动生成代码的对象
            InstantStateProcessor processor = mInstantStateMap.get(fullClassName);
            if (processor == null) {
                processor = new InstantStateProcessor(messager,encloseElement);
                mInstantStateMap.put(fullClassName, processor);
            }
            processor.addField(element);
        }

        for(InstantStateProcessor processor : mInstantStateMap.values()){
            processor.generate(filer,elementUtils);
        }

        //遍历生成java代码
        //for(HelperClass helperClass : mInstantStateMap.values()){
        //    try {
        //
        //        //获取helperClass，调用其方法直接生成java代码
        //        JavaFile javaFile = helperClass.generateCode();
        //        if(javaFile != null){
        //            javaFile.writeTo(filer);
        //        }
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //}

        return true;
    }


    private void getHelperClass(Map<String, HelperClass> mHelperClassMap, Element element) {
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
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(InstanceState.class.getCanonicalName());
        set.add(IntentParam.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_6;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    private void print(String template, Object... args){
        if(args.length == 0){
            System.out.println(template);
        }else{
            System.out.println(String.format(template,args));
        }
    }
}