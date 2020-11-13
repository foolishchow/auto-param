package me.foolishchow.anrdoid.processor.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Description: <br/>
 * Author: foolishchow <br/>
 * Date: 13/11/2020 11:12 AM <br/>
 */
public abstract class BaseAnnotationProcessor {
    public final Messager mMessager;
    public final TypeElement mOriginClass;
    //public String mPackageName;

    public BaseAnnotationProcessor(Messager messager,TypeElement originClass) {
        mMessager = messager;
        mOriginClass = originClass;
    }

    public void printError(String msg, Element element) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg, element);
    }

    public void printInfo(String msg,  Element element) {
        mMessager.printMessage(
                Diagnostic.Kind.OTHER, msg, element);
        //mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }


    public void generate(Filer filer, Elements elements){
        String originClassName = mOriginClass.getSimpleName().toString();
        String targetClassName = getTargetClassName(originClassName);
        String packageName =
                elements.getPackageOf(mOriginClass).getQualifiedName().toString();

        ClassName targetClassType = ClassName.get(packageName, targetClassName);

        TypeSpec.Builder builder = TypeSpec.classBuilder(targetClassName)
                .addModifiers(Modifier.PUBLIC);
        process(
                elements,
                builder,
                packageName,
                originClassName,
                ClassName.get(packageName, originClassName),
                targetClassName,
                targetClassType
        );

        JavaFile javaFile = JavaFile.builder(packageName, builder.build()).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public abstract String getTargetClassName(String originClassName);



    public abstract void process(
            Elements elements,
            TypeSpec.Builder builder,
            String packageName,
            String originClassName,
            ClassName originClassType,
            String targetClassName,
            ClassName targetClassType
    );


    private static Pattern mPattern = Pattern.compile("^m");

    public String getSetMethodName(String fieldName) {
        Matcher matcher = mPattern.matcher(fieldName);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return captureName(sb.toString());
    }

    //首字母大写
    public static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public String escapeString(String fieldName) {
        try {
            byte[] md5s = MessageDigest.getInstance("MD5").digest(fieldName.getBytes("utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : md5s) {
                stringBuilder.append(String.format("%02x", new Integer(b & 0xff)));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fieldName;
    }


    public String wrapString(String value) {
        return String.format("\"%s\"", value);
    }

    public String format(String template, Object... value) {
        return String.format(template, value);
    }

    private static Pattern camelPattern = Pattern.compile("[A-Z]");

    public static String camel2snake(String str) {
        return camel2snake(str, true);
    }

    public static String camel2snake(String str, boolean uppercase) {
        Matcher matcher = camelPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        if (uppercase) {
            return sb.toString().toUpperCase();
        }
        return sb.toString();
    }


}
