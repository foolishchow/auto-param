package me.foolishchow.android.navigation

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.squareup.javapoet.*
import groovy.util.Node
import groovy.util.XmlParser
import groovy.xml.QName
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.closureOf
import java.io.File
import java.util.regex.Pattern
import javax.lang.model.element.Modifier

class NavigationPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val android: BaseAppModuleExtension = project.extensions.getByName("android") as BaseAppModuleExtension

        val mainSourceSet = android.sourceSets
                .maybeCreate("main")
        android.buildTypes.forEach { buildType ->
            mainSourceSet
                    .java.srcDir("${project.buildDir}/generated/navigation/${buildType.name}/out")

            project.tasks.create(mapOf(
                    "name" to "navigationTransformTask${buildType.name}",
                    "group" to "auto-param",
                    "type" to NavigationTransformTask::class.java
            ), closureOf<NavigationTransformTask> {
                val files = mutableListOf<File>()
                android.sourceSets.forEach { sourceSet ->
                    val res = sourceSet.res
                    res.srcDirs.forEach { dir ->
                        project.fileTree("$dir/navigation").forEach { file ->
                            files.add(file)
                        }
                    }
                }
                this.inputs.property("buildType", buildType.name)
                this.inputs.files(files.toTypedArray())
            })
        }
    }
}

open abstract class NavigationTransformTask : DefaultTask() {

    @InputFiles
    abstract fun getNavFiles(): ConfigurableFileCollection

    private lateinit var android: BaseAppModuleExtension
    private lateinit var packageName: String
    private lateinit var mBuildType: String

    @TaskAction
    open fun perform() {
        android = project.extensions.getByName("android") as BaseAppModuleExtension
        packageName = android.defaultConfig.applicationId

        println("navigation-transform-task perform")
        mBuildType = inputs.properties["buildType"] as String
        inputs.files.forEach { file ->
            println(file.absolutePath)
            parseAndGenerate(file)
        }
    }

    fun parseAndGenerate(file: File) {
        val name = getClassName(file.name.replace(".xml", "")) + "Navigation"

        val NavGraph = ClassName.get("me.foolishchow.androidplugins.fake", "BaseGraph")
        val NavController = ClassName.get("androidx.navigation", "NavController")
        val Resource = ClassName.get(packageName, "R")
        val navigationClass = TypeSpec.classBuilder(name)
                .superclass(NavGraph)
                .addModifiers(Modifier.PUBLIC)


        val navigation = XmlParser().parse(file)
        navigation.loopAttribute { attr ->
            val name = attr.key
            val value = attr.value
            if (name.isResId) {
                val field = FieldSpec.builder(
                        TypeName.INT,
                        "id",
                        Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC
                ).initializer("\$T.id.${value.resId}", Resource)
                navigationClass.addField(field.build())
            }
            println("${name.localPart} ${name.namespaceURI} ${name.prefix} ${name.qualifiedName}")
            println(attr.value)
        }

        val constructor = MethodSpec.constructorBuilder()
                .addParameter(NavController, "navController")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(navController)");

        navigation.children().forEach { node ->
            val fragment = if (node is Node) node else return@forEach
            if (!fragment.name().equals("fragment")) return@forEach
            var id = ""
            var className = ""
            var label = ""
            fragment.loopAttribute { attr ->
                when {
                    attr.key.isResId -> {
                        id = attr.value.resId
                    }
                    attr.key.isAndroidName -> {
                        className = attr.value
                    }
                    attr.key.isAndroidLabel -> {
                        label = attr.value
                    }
                }
            }
            constructor.addStatement("addDestination(${className}.class,R.id.${id},\"$label\")")


            fragment.children().forEach{node->
                val action = if (node is Node) node else return@forEach
                if (!action.name().equals("action")) return@forEach
                var actionId = ""
                var actionDest = ""
                action.loopAttribute { attr ->
                    when{
                        attr.key.isResId -> {
                            actionId = attr.value.resId
                        }
                        attr.key.isAppDestination -> {
                            actionDest = attr.value.resId
                        }
                    }
                }
                android.buildTypes.forEach { buildType ->
                    if(buildType.name != mBuildType) return
                    //buildType.addResValue()
                }
                android.defaultConfig {
                    //addResValue()
                }
                constructor.addStatement("addAction(R.id.${actionId},R.id.${id},R.id.${actionDest})")
            }
        }


        navigationClass.addMethod(constructor.build())
        val javaFile = JavaFile.builder(packageName + ".navigation", navigationClass.build())
                .build()
        val dst = File(project.buildDir, "generated/navigation/$mBuildType/out")
        if (!dst.exists()) {
            dst.mkdirs()
        }
        javaFile.writeTo(dst)
    }


    val camelPattern = Pattern.compile("_[a-z]")


    private fun getClassName(str: String): String {
        return snake2camel(str)
    }

    private fun snake2camel(str: String): String {
        val matcher = camelPattern.matcher(str);
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(0).substring(1).toUpperCase());
        }
        matcher.appendTail(sb)
        sb.setCharAt(0, sb[0].toUpperCase())
        return sb.toString();
    }

    protected fun getIncremental(): Boolean {
        return true
    }
}


const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
const val APP_NAMESPACE = "http://schemas.android.com/apk/res-auto"

fun Node.loopAttribute(predicate: (Map.Entry<QName, String>) -> Unit) {
    val attributes = this.attributes() as Map<QName, String>
    attributes.forEach(predicate)
}

val QName.isAppDestination:Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("destination")
    }
val QName.isAndroidLabel: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("label")
    }
val QName.isAndroidName: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("name")
    }

val QName.isResId: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("id")
    }

val QName.isStartDestination: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("startDestination")
    }

val String.resId: String
    get() {
        return this.replace("+", "").replace("@id/", "")
    }