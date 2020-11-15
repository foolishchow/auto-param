package me.foolishchow.android.viewbinding

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ViewBindingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        BaseExtension android = project.extensions.getByName('android')

        def outputs = []

        def outDir = "${project.buildDir}/generated/me_foolishchow_android_view_binding/debug/out"
        //,dependsOn: "assembleDebug"
        def generatedFileDir = new File(outDir)
        def task = project.task("askForViewBinding") {
            //task.outputs.dir = generatedFileDir
            doLast {
                generatedFileDir.mkdirs()
                for (int i = 0; i < 10; i++) {
                    new File(generatedFileDir, "${i}.txt").text = i
                }
            }
        }
        android.buildTypes.each {buildType->
            def outDir1 = "${project.buildDir}/generated/me_foolishchow_android_view_binding/"+
                    "${buildType.name}/out"
            task.outputs.dir(new File(outDir1))
            println("buildType.name ===> ${buildType.name}")
        }

        project.afterEvaluate {
            //project.
        }


        println(project.getDefaultTasks())
        //project.getDefaultTasks("preBuild"){
        //    doLast{
        //        println("preBuild ===> preBuild")
        //    }
        //}
        project.beforeEvaluate {
            println("beforeEvaluate ===> beforeEvaluate")
        }

        //project.asSynchronized()



        //task.mustRunAfter()
    }

    private void createTask(){

    }

}