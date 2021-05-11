package me.foolishchow.android.navigationtransform

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.NewIncrementalTask
import com.android.tools.r8.code.M
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import groovy.xml.QName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges
import org.jetbrains.annotations.NotNull

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.lang.model.element.Modifier

public abstract class NavigationTransformTask extends DefaultTask {


    @InputFiles()
    public abstract ConfigurableFileCollection getNavFiles();

    @TaskAction
    public void perform() {
        println "navigation-transform-task perform"
        BaseAppModuleExtension android = project.extensions.getByType(AppExtension)

        String buildType = getInputs().getProperties().get("buildType")
        getInputs().each { input ->
            input.files.each { file ->
                generateFile(project, buildType,file)
            }
        }

    }

    private static void generateFile(Project project,String buildType, File file) {
        BaseAppModuleExtension android = project.extensions.getByType(AppExtension)
        String packageName = android.defaultConfig.applicationId

        String name = getClassName(file.name.replace(".xml", "")) + "Navigation";

        ClassName NavGraph = ClassName.get("me.foolishchow.androidplugins.fake","BaseGraph")
        ClassName NavController = ClassName.get("androidx.navigation","NavController")
        ClassName Resource = ClassName.get(packageName, "R")
        TypeSpec.Builder navigationClass = TypeSpec.classBuilder(name)
        .superclass(NavGraph)
                .addModifiers(Modifier.PUBLIC);


        Node navigation = new XmlParser().parse(file)
        //print navigation.attribute("android:id")
        navigation.attributes().keySet().each { key ->
            println "   ${key.localPart} ${navigation.attribute(key)}"


            if(key.localPart == "id"){
                String id = getId(navigation.attribute(key))
                FieldSpec.Builder field = FieldSpec.builder(
                        TypeName.INT,
                        "id",
                        Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
                ).initializer('$T.id.'+id,Resource)
                navigationClass.addField(field.build())
            }
        }

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
            .addParameter(NavController,"navController")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(navController)");
        println "fragment----------start "
        navigation.children().each { fragment ->
            Node node = fragment
            println node.name()
            String id = ""
            String className = ""
            String label = ""
            fragment.attributes().keySet().each { key ->
                if(key.localPart == "id"){
                    id = getId(fragment.attribute(key))
                }
                if(key.localPart == "name"){
                    className = fragment.attribute(key)
                }
                if(key.localPart == "label"){
                    label = fragment.attribute(key)
                }
            }
            ClassName f = ClassName.bestGuess(className)
            constructor.addStatement('addDestination($T.class,'+"R.id.${id},\"$label\")",f)

            println "action----------start"
            fragment.children().each { action ->
                String actionId = ""
                String actionDest = ""
                action.attributes().keySet().each { key ->
                    if(key.localPart == "id"){
                        actionId = getId(action.attribute(key))
                    }
                    if(key.localPart == "destination"){
                        actionDest = getId(action.attribute(key))
                    }
                    println "           ${key.localPart} ${action.attribute(key)}"
                }
                constructor.addStatement("addAction(R.id.${actionId},R.id.${id},R.id.${actionDest})")
            }
            println "action----------end"
        }
        println "fragment----------end "


        navigationClass.addMethod(constructor.build())

        JavaFile javaFile = JavaFile.builder(packageName + ".navigation", navigationClass.build())
                .build()
        File dst = new File(project.buildDir, "generated/navigation/$buildType/out")
        if (!dst.exists()) {
            dst.mkdirs()
        }


        javaFile.writeTo(dst)


    }

    private static String getId(String id){
        return id.replace("+","").replace("@id/","")
    }

    private static Pattern camelPattern = Pattern.compile("_[a-z]");

    private static String getClassName(String str) {
        return snake2camel(str)
    }

    public static String snake2camel(String str) {
        Matcher matcher = camelPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(0).substring(1).toUpperCase());
        }
        matcher.appendTail(sb);
        sb.setCharAt(0, sb.charAt(0).toUpperCase())
        return sb.toString();
    }

    protected boolean getIncremental() {
        return true;
    }


}