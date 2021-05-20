package me.foolishchow.android.navigationprocessor

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import groovy.util.Node
import groovy.util.XmlParser
import me.foolishchow.android.navigationprocessor.extensions.navigationDir
import me.foolishchow.android.navigationprocessor.extensions.resId
import me.foolishchow.android.navigationprocessor.extensions.resourceSymbol
import me.foolishchow.android.navigationprocessor.extensions.snake2camel
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.lang.model.element.Modifier


abstract class NavigationTask : DefaultTask() {

    @InputFiles
    abstract fun getNavFiles(): ConfigurableFileCollection

    private var outputDir:File? = null

    @OutputDirectory
    open fun getOutputDir(): File? {
        return outputDir
    }

    private lateinit var android: BaseAppModuleExtension
    private lateinit var packageName: String
    private lateinit var mVariantName: String

    private lateinit var mDir: File
    private lateinit var mClass: TypeSpec.Builder
    private val mMaps = mutableMapOf<String, String>()
    private lateinit var mResource: ClassName

    @TaskAction
    open fun perform() {
        android = project.extensions.getByName("android") as BaseAppModuleExtension
        packageName = android.defaultConfig.applicationId

        mVariantName = inputs.properties["variant"] as String

        android.applicationVariants.forEach{ variant->
            val file = File(project.navigationDir(variant))
            if (variant.name.equals(mVariantName)) {
                mDir = file
            }
        }

        mResource = ClassName.get(packageName, "R")
        mClass = TypeSpec.classBuilder("NavigationManager")
                //.superclass(NavGraph)
                .addModifiers(Modifier.PUBLIC)
        inputs.files.forEach { file ->
            parseAndGenerate(file)
        }


        val method = MethodSpec.methodBuilder("attach")
                .addParameter(NavController, "controller")
                .addParameter(ClassName.INT, "navigationId")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .beginControlFlow("switch(navigationId)")



        mMaps.forEach { item ->
            method.addStatement("case \$T.navigation.${item.key}:", mResource)
            method.addStatement("${item.value}(controller)")
            method.addStatement("break")
        }
        method.endControlFlow()
        mClass.addMethod(method.build())

        val javaFile = JavaFile.builder("$packageName.navigation", mClass.build())
                .build()
        if (!mDir.exists()) {
            mDir.mkdirs()
        }
        javaFile.writeTo(mDir)
    }


    private fun parseAndGenerate(file: File) {
        val name = file.xmlName.snake2camel()
        val methodName = "attachNavigation$name"
        mMaps[file.xmlName] = methodName
        val method = MethodSpec.methodBuilder(methodName)
                .addParameter(NavController, "controller")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)

        method.addStatement("\$T fragment", FragmentDestination)
        method.addStatement("\$T action", NavAction)
        method.addStatement("\$T.Builder builder", NavOptionsBuilder)
        method.addStatement(
                "\$T graph = controller.getNavigatorProvider().getNavigator(\$T.class).createDestination()",
                NavGraph, NavGraphNavigator
        )

        val navigation = XmlParser().parse(file)
        navigation.loopAttribute { attr ->
            val value = attr.value
            if (attr.key.isResId) {

                method.addStatement("graph.setId(\$T.id.${value.resId})", mResource)
            }
            if (attr.key.isStartDestination) {
                method.addStatement("graph.setStartDestination(\$T.id.${value.resId})", mResource)
            }
            if (attr.key.isAndroidLabel) {
                method.addStatement("graph.setLabel(\"${attr.value}\")", mResource)
            }
            println(attr.value)
        }


        navigation.children().forEach { node ->
            if (node is Node) {
                parseFragment(node, method)
            }
        }

        method.addStatement("controller.setGraph((\$T)graph)", NavGraph)

        mClass.addMethod(method.build())

    }


    private fun parseFragment(fragment: Node, method: MethodSpec.Builder) {
        if (!fragment.name().equals("fragment")) return

        method.addStatement(
                "fragment = controller.getNavigatorProvider().getNavigator(\$T.class).createDestination()",
                FragmentNavigator)
        fragment.loopAttribute { attr ->
            when {
                attr.key.isResId -> {
                    method.addStatement("fragment.setId(\$T.id.${attr.value.resId})", mResource)
                }
                attr.key.isAndroidName -> {
                    method.addStatement("fragment.setClassName(${attr.value}.class.getName())")
                }
                attr.key.isAndroidLabel -> {
                    method.addStatement("fragment.setLabel(\"${attr.value}\")")
                }
            }
        }

        fragment.children().forEach { actionNode ->
            if (actionNode is Node && actionNode.name().equals("action")) {
                val action: Node = actionNode
                parseAction(method, action)
            }
        }
        method.addStatement("graph.addDestination(fragment)")

    }

    private fun parseAction(method: MethodSpec.Builder, action: Node) {
        method.addStatement("builder = new \$T.Builder()", NavOptionsBuilder)


        var actionId = ""
        var destId = ""
        var popUpTo = ""
        var popUpToInclusive = "false"
        action.loopAttribute { attr ->
            when {
                attr.key.isResId -> {
                    actionId = attr.value.resId
                }
                attr.key.isAppDestination -> {
                    destId = attr.value.resId
                }
                attr.key.isPopEnterAnim -> {
                    method.addStatement(
                            "builder.setPopEnterAnim(${attr.value.resourceSymbol})",
                            mResource
                    )
                }
                attr.key.isPopExitAnim -> {
                    method.addStatement(
                            "builder.setPopExitAnim(${attr.value.resourceSymbol})",
                            mResource
                    )
                }
                attr.key.isEnterAnim -> {
                    method.addStatement(
                            "builder.setEnterAnim(${attr.value.resourceSymbol})",
                            mResource
                    )
                }
                attr.key.isExitAnim -> {
                    method.addStatement(
                            "builder.setExitAnim(${attr.value.resourceSymbol})",
                            mResource
                    )
                }

                attr.key.isLaunchSingleTop -> {
                    method.addStatement(
                            "builder.setLaunchSingleTop(${attr.value.resourceSymbol})",
                            mResource
                    )
                }

                attr.key.isPopUpTo -> {
                    popUpTo = attr.value.resourceSymbol
                }

                attr.key.isPopUpToInclusive -> {
                    popUpToInclusive = attr.value.resourceSymbol
                }
            }
        }

        if (popUpTo.isNotEmpty()) {
            method.addStatement(
                    "builder.setPopUpTo(${popUpTo},${popUpToInclusive})",
                    mResource
            )
        }

        method.addStatement("action = new \$T(\$T.id.$destId,builder.build())",
                NavAction, mResource)
        method.addStatement("fragment.putAction(\$T.id.${actionId},action)",
                mResource)
    }


    protected fun getIncremental(): Boolean {
        return true
    }

}