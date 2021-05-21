package me.foolishchow.android.plugin.navigation

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import groovy.util.Node
import groovy.util.XmlParser
import me.foolishchow.android.plugin.navigation.extensions.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.lang.model.element.Modifier
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


abstract class NavigationTask : DefaultTask() {



    @InputFiles
    abstract fun getNavFiles(): ConfigurableFileCollection

    @get:OutputDirectory
    lateinit var javaDir: File

    @get:OutputDirectory
    lateinit var resDir: File

    @get:OutputFile
    lateinit var ruleFile: File

    private lateinit var mClass: TypeSpec.Builder

    /**
     * 缓存当前所有的文件名和方法名
     */
    private val mFileNameWithMethodName = mutableMapOf<File, String>()
    private lateinit var mResource: ClassName

    @TaskAction
    open fun perform() {
        val android = project.extensions.getByName("android") as BaseAppModuleExtension
        val packageName = android.defaultConfig.applicationId

        project.delete(javaDir.listFiles())
        project.delete(resDir.listFiles())
        project.delete(ruleFile)


        parseFiles(packageName)

        createDiscard()

        crateFileNames()

    }


    private fun parseFiles(packageName: String?) {
        mResource = ClassName.get(packageName, "R")
        mClass = TypeSpec.classBuilder("NavigationManager")
                .addModifiers(Modifier.PUBLIC)
        inputs.files.forEach { file ->
            parseAndGenerate(file)
        }

        val method = MethodSpec.methodBuilder("attach")
                .addParameter(NavController, "controller")
                .addParameter(ClassName.INT, "navigationId")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .beginControlFlow("switch(navigationId)")

        mFileNameWithMethodName.forEach { item ->
            method.addStatement("case \$T.navigation.${item.key.xmlName}:", mResource)
            method.addStatement("   ${item.value}(controller)")
            method.addStatement("break")
        }
        method.endControlFlow()
        mClass.addMethod(method.build())

        val javaFile = JavaFile.builder("$packageName.navigation", mClass.build())
                .build()
        if (!javaDir.exists()) {
            javaDir.mkdirs()
        }
        javaFile.writeTo(javaDir)
    }


    private fun parseAndGenerate(file: File) {
        val name = file.xmlName.snake2camel()
        val methodName = "attachNavigation$name"
        mFileNameWithMethodName[file] = methodName
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
                    val type = ClassName.bestGuess(attr.value)
                    method.addStatement("fragment.setClassName(\$T.class.getName())", type)
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

        method.addStatement(
                "action = new \$T(\$T.id.$destId,builder.build())",
                NavAction, mResource
        )
        method.addStatement(
                "fragment.putAction(\$T.id.${actionId},action)", mResource
        )
    }


    private fun crateFileNames() {
        ruleFile.delete()
        val fw = FileWriter(ruleFile.absoluteFile)
        val bw = BufferedWriter(fw)
        mFileNameWithMethodName.forEach { attr ->
            bw.write(attr.key.absolutePath)
            bw.newLine()
        }
        bw.close()
    }

    private fun createDiscard() {
        val document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument()

        document.xmlStandalone = false

        val resources = document.createElement("resources")
        resources.setAttribute("xmlns:tools", "http://schemas.android.com/tools")
        val discards = mutableListOf<String>()
        mFileNameWithMethodName.forEach { item ->
            discards.add("@navigation/${item.key.xmlName}")
        }
        resources.setAttribute("tools:discard", discards.joinToString(separator = ",") { it })
        document.appendChild(resources)

        val rawDir = File(resDir, "raw")
        if (!rawDir.exists()) {
            rawDir.mkdirs()
        }
        val discardFile = File(rawDir, "nav_discard.xml")

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        //是否自动换行
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(DOMSource(document), StreamResult(discardFile))
    }


    protected fun getIncremental(): Boolean {
        return false
    }

}