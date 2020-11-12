package me.foolishchow.anrdoid.processor.activity;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class TypeMap {

    private TypeElement mEncloseElement;



    private ArrayList<Element> mElements;

    public ArrayList<Element> getElements() {
        return mElements;
    }

    public TypeMap(TypeElement encloseElement) {
        this.mEncloseElement = encloseElement;
    }


    public void addElement(Element element){
        if(mElements == null){
            mElements = new ArrayList<>();
        }
        mElements.add(element);

    }


    public void generate(Filer filer, Elements elements) {
        String className = mEncloseElement.getSimpleName().toString();
        String simpleClassName = className.replace("Activity","") + "Intent";
        String packageName =
                elements.getPackageOf(mEncloseElement).getQualifiedName().toString();

        ClassName intentClassName = ClassName.get("android.content", "Intent");

        TypeSpec.Builder builder = TypeSpec.classBuilder(simpleClassName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(intentClassName);


        MethodSpec.Builder parse = MethodSpec
                .methodBuilder("parseIntent")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addParameter(ClassName.get(packageName, className), "activity")
                .addParameter(intentClassName,"intent");
        builder.addMethod(parse.build());


        for(Element element: mElements){
            String fieldName = element.getSimpleName().toString();
            FieldSpec.Builder field = FieldSpec.builder(
                    ClassName.get("java.lang", "String"),
                    camel2snake(fieldName),
                    Modifier.PRIVATE,Modifier.FINAL
            );

            String name = escapeString(fieldName, fieldName);
            field.initializer(wrapString(name));
            builder.addField(field.build());

            TypeMirror typeMirror = element.asType();
            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(getSetMethodName(fieldName))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(packageName, simpleClassName))
                    .addParameter(TypeName.get(typeMirror),"param")
                    .addStatement(format("putExtra(%s,param)",camel2snake(fieldName)))
                    .addStatement("return this");

            builder.addMethod(method.build());
        }


        //通过TypeSpec获取javaFile对象，用于生成代码
        JavaFile javaFile = JavaFile.builder(packageName, builder.build()).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //System.out.println("============="+typeMap.getName());
        //for(Element el : typeMap.getElements()){
        //    print("=======name %s , class %s , package %s",
        //            el.getSimpleName(),
        //            typeMap.getName(),
        //            elements.getPackageOf(el).getQualifiedName().toString()
        //    );
        //}
    }


    private static Pattern mPattern = Pattern.compile("^m");
    private String getSetMethodName(String fieldName){
        Matcher matcher = mPattern.matcher(fieldName);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return "set"+sb.toString();
    }
    private String escapeString(String fieldName, String name)  {
        try {
            byte[] md5s = MessageDigest.getInstance("MD5").digest(fieldName.getBytes("utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : md5s) {
                stringBuilder.append(String.format("%02x", new Integer(b & 0xff)));
            }
            return  stringBuilder.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }


    private String wrapString(String value){
        return String.format("\"%s\"",value);
    }

    private String format(String template,Object... value){
        return String.format(template,value);
    }


    private static Pattern camelPattern = Pattern.compile("[A-Z]");
    /** 驼峰转下划线,效率比上面高 */
    public static String camel2snake(String str){
        return camel2snake(str,true);
    }

    public static String camel2snake(String str,boolean uppercase) {
        Matcher matcher = camelPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        if(uppercase){
            return sb.toString().toUpperCase();
        }
        return sb.toString();
    }
}
