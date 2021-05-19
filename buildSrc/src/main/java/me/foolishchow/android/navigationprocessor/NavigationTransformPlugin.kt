package me.foolishchow.android.navigationprocessor

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.getProguardFiles
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.closureOf
import java.io.File

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 1:51 PM
 */

fun Project.navigationDir(buildType: BuildType): String {
    return "${project.buildDir}/generated/source/navigation/${buildType.name}"
}

class NavigationTransformPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val android: BaseAppModuleExtension = project.extensions.getByName("android") as BaseAppModuleExtension


        val mainSourceSet = android.sourceSets
                .maybeCreate("main")
        android.buildTypes.forEach { buildType ->
            mainSourceSet
                    .java.srcDir(project.navigationDir(buildType))

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


        android.sourceSets.forEach { sourceSet ->
            val res = sourceSet.res
            res.srcDirs.forEach { dir ->
                project.fileTree("$dir/navigation").forEach { file ->
                    println("exclude /res/navigation/${file.name}")
                    android.packagingOptions.exclude("/res/navigation/${file.name}")
                }
            }
        }

        project.afterEvaluate{
            android.buildTypes.forEach{buildType->
                buildType.proguardFiles.forEach{file->
                    println(file.absolutePath)
                }
            }

        }
    }
}

