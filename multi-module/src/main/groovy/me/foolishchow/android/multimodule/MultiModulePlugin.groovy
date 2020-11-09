package me.foolishchow.android.multimodule

import com.android.build.gradle.BaseExtension
import com.android.manifmerger.ManifestMerger2
import com.android.manifmerger.MergingReport
import com.android.manifmerger.XmlDocument
import com.android.utils.ILogger

import me.foolishchow.android.multimodule.dependence.DefaultDependenceExtension
import me.foolishchow.android.multimodule.dependence.IDependenceExtension
import me.foolishchow.android.multimodule.mutilmodule.IMultiModulePluginExtension
import me.foolishchow.android.multimodule.mutilmodule.MultiModulePluginExtension
import me.foolishchow.android.multimodule.mutilmodule.OnModuleIncludeListener
import me.foolishchow.android.multimodule.mutilmodule.ProductFlavorInfo
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiModulePlugin implements Plugin<Project> {
    Project project
    ProductFlavorInfo productFlavorInfo;

    String startTaskState = NORMAL

    private final static String NORMAL = 'normal'
    private final static String ASSEMBLE_OR_GENERATE = 'assemble_or_generate'


    @Override
    void apply(Project project) {
        this.project = project
        productFlavorInfo = new ProductFlavorInfo(project);
        project.extensions.create(
                IDependenceExtension, 'dependenceManager',
                DefaultDependenceExtension,
                project)


        def extension = project.extensions.create(
                IMultiModulePluginExtension, 'pins',
                MultiModulePluginExtension,
                project)

        extension.addModuleIncludeListener(new OnModuleIncludeListener() {
            @Override
            void addIncludeModule(String projectName) {
                println("multi-module  add -->$projectName")
                addMicroModuleSourceSet(projectName)
                //generateAndroidManifest()
            }
        });


        project.afterEvaluate {
            //println("multi-module  afterEvaluate -->${extension.projects}")
            generateAndroidManifest()
            //project.tasks.preBuild.doFirst {
            //    println("multi-module  preBuild -->${extension.projects}")
            //    generateAndroidManifest()
            //}
        }
        //extension.getProjects().each {projectName->
        //    addVariantSourceSet(projectName,"main")
        //}
        //project.afterEvaluate{
        //    project.buildDir
        //    File mainManifestFile = new File(microModuleInfo.mainMicroModule.microModuleDir, "/src/${variantName}/AndroidManifest.xml")
        //    ManifestMerger2.Invoker invoker = new ManifestMerger2.Invoker(mainManifestFile, logger, mergeType, documentType)
        //
        //}
        //def extension  = project.extensions.create('greeting',GreetingPluginExtension)

        //project.task('hello'){
        //    doLast {
        //        println extension.message
        //    }
        //}
    }


    def addMicroModuleSourceSet(String microModule) {
        addVariantSourceSet(microModule, 'main')

        productFlavorInfo.buildTypes.each {
            addVariantSourceSet(microModule, it)
        }

        if (!productFlavorInfo.singleDimension) {
            productFlavorInfo.productFlavors.each {
                addVariantSourceSet(microModule, it)
            }
        }

        productFlavorInfo.combinedProductFlavors.each {
            addVariantSourceSet(microModule, it)
            def flavorName = it
            productFlavorInfo.buildTypes.each {
                addVariantSourceSet(microModule, flavorName + Utils.upperCase(it))
            }
        }

        //def testTypes = ['androidTest', 'test']
        //testTypes.each {
        //    def testType = it
        //    addVariantSourceSet(microModule, testType)
        //
        //    if (testType == 'test') {
        //        productFlavorInfo.buildTypes.each {
        //            addVariantSourceSet(microModule, testType + Utils.upperCase(it))
        //        }
        //    } else {
        //        addVariantSourceSet(microModule, testType + 'Debug')
        //    }
        //
        //    if (!productFlavorInfo.singleDimension) {
        //        productFlavorInfo.productFlavors.each {
        //            addVariantSourceSet(microModule, testType + Utils.upperCase(it))
        //        }
        //    }
        //
        //    productFlavorInfo.combinedProductFlavors.each {
        //        def productFlavorName = testType + Utils.upperCase(it)
        //        addVariantSourceSet(microModule, productFlavorName)
        //
        //        if (testType == 'test') {
        //            productFlavorInfo.buildTypes.each {
        //                addVariantSourceSet(microModule, productFlavorName + Utils.upperCase(it))
        //            }
        //        } else {
        //            addVariantSourceSet(microModule, productFlavorName + 'Debug')
        //        }
        //    }
        //}
    }


    def addVariantSourceSet(String microModule, String type) {
        //println("$project.projectDir===>$microModule")

        def absolutePath = project.projectDir.getAbsolutePath();
        BaseExtension android = project.extensions.getByName('android')
        def obj = android.sourceSets.findByName(type)
        if (obj == null) {
            obj = android.sourceSets.create(type)
        }
        //obj.java.srcDir( "src/${microModule}/java")
        //obj.res.srcDir( "src/${microModule}/res")

        obj.java.srcDir(absolutePath + "/${microModule}/src/main/java")
        obj.java.srcDir(absolutePath + "/${microModule}/src/main/kotlin")
        obj.res.srcDir(absolutePath + "/${microModule}/src/main/res")
        obj.jni.srcDir(absolutePath + "/${microModule}/src/main/jni")
        obj.jniLibs.srcDir(absolutePath + "/${microModule}/src/main/jniLibs")
        obj.aidl.srcDir(absolutePath + "/${microModule}/src/main/aidl")
        obj.assets.srcDir(absolutePath + "/${microModule}/src/main/assets")
        obj.shaders.srcDir(absolutePath + "/${microModule}/src/main/shaders")
        obj.resources.srcDir(absolutePath + "/${microModule}/src/main/resources")
        obj.renderscript.srcDir(absolutePath + "/${microModule}/src/main/rs")
    }


    def generateAndroidManifest() {
        mergeAndroidManifest()
        //if ((startTaskState == ASSEMBLE_OR_GENERATE || !microModuleInfo.exportMicroModules.isEmpty()) && isMainSourceSetEmpty()) {
        //    setMainSourceSetManifest()
        //    return
        //}
        //
        //productFlavorInfo.buildTypes.each {
        //    mergeAndroidManifest(it)
        //}
        //
        //if (!productFlavorInfo.singleDimension) {
        //    productFlavorInfo.productFlavors.each {
        //        mergeAndroidManifest(it)
        //    }
        //}
        //
        //productFlavorInfo.combinedProductFlavors.each {
        //    mergeAndroidManifest(it)
        //
        //    def productFlavor = it
        //    productFlavorInfo.buildTypes.each {
        //        mergeAndroidManifest(productFlavor + Utils.upperCase(it))
        //    }
        //}
        //
        //def androidTest = 'androidTest'
        //mergeAndroidManifest(androidTest)
        //mergeAndroidManifest(androidTest + 'Debug')
        //if (!productFlavorInfo.singleDimension) {
        //    productFlavorInfo.productFlavors.each {
        //        mergeAndroidManifest(androidTest + Utils.upperCase(it))
        //    }
        //}
        //productFlavorInfo.combinedProductFlavors.each {
        //    mergeAndroidManifest(androidTest + Utils.upperCase(it))
        //    mergeAndroidManifest(androidTest + Utils.upperCase(it) + 'Debug')
        //}
    }


    def mergeAndroidManifest() {
        def projectDir = project.projectDir.getAbsolutePath()
        File mainManifestFile = new File( "${projectDir}/src/main/AndroidManifest.xml")
        if (!mainManifestFile.exists()) {
            mainManifestFile.createNewFile()
        }
        BaseExtension e = project.extensions.getByName('android')

        mainManifestFile.write("""<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="${e.defaultConfig.applicationId}">
       <application></application>
</manifest>""")
        //println(mainManifestFile.getAbsolutePath())
        //if (!mainManifestFile.exists()) return
        ManifestMerger2.MergeType mergeType = ManifestMerger2.MergeType.APPLICATION
        XmlDocument.Type documentType = XmlDocument.Type.MAIN
        def logger = new ILogger() {
            @Override
            void error(Throwable t, String msgFormat, Object... args) {
                println(msgFormat)
            }

            @Override
            void warning(String msgFormat, Object... args) {

            }

            @Override
            void info(String msgFormat, Object... args) {

            }

            @Override
            void verbose(String msgFormat, Object... args) {

            }
        }
        ManifestMerger2.Invoker invoker = new ManifestMerger2.Invoker(mainManifestFile, logger, mergeType, documentType)
        invoker.withFeatures(ManifestMerger2.Invoker.Feature.NO_PLACEHOLDER_REPLACEMENT)

        MultiModulePluginExtension extension = project.extensions.getByName('pins')
        extension.getProjects().each { module ->
            if (startTaskState == ASSEMBLE_OR_GENERATE) return
            if (module == "main") return
            def microManifestFile = new File("${projectDir}/${module}/src/main/AndroidManifest.xml")
            if (microManifestFile.exists()) {
                invoker.addLibraryManifest(microManifestFile)
            }

        }

        def mergingReport = invoker.merge()
        if (!mergingReport.result.success) {
            mergingReport.log(logger)
            throw new GradleException(mergingReport.reportString)
        }
        def moduleAndroidManifest = mergingReport.getMergedDocument(MergingReport.MergedManifestKind.MERGED)
        moduleAndroidManifest = new String(moduleAndroidManifest.getBytes('UTF-8'))
        mainManifestFile.write(moduleAndroidManifest)
        //def saveDir = new File(project.projectDir, "src/")
        //saveDir.mkdirs()
        //def AndroidManifestFile = new File(saveDir, 'AndroidManifest.xml')
        //AndroidManifestFile.createNewFile()
        //AndroidManifestFile.write(moduleAndroidManifest)

        def extensionContainer = project.getExtensions()
        BaseExtension android = extensionContainer.getByName('android')
        def obj = android.sourceSets.findByName("main")
        if (obj == null) {
            return
        }
        obj.manifest.srcFile mainManifestFile.absolutePath
    }
}