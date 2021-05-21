package me.foolishchow.android.plugin.navigation

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import me.foolishchow.android.plugin.navigation.extensions.NavigationTaskName
import me.foolishchow.android.plugin.navigation.extensions.navJavaDir
import me.foolishchow.android.plugin.navigation.extensions.navResDir
import me.foolishchow.android.plugin.navigation.extensions.navRuleFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.closureOf
import java.io.*

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 1:51 PM
 */


@Suppress("DefaultLocale", "UnstableApiUsage")
class NavigationPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val android: BaseAppModuleExtension = project.extensions.getByName("android") as BaseAppModuleExtension

        android.applicationVariants.configureEach {
            registerPostProcessProguardRules(this, project)
            registerNavigationTask(this, project)
        }
    }


    private fun registerNavigationTask(variant: ApplicationVariant, _project: Project) {
        val android: BaseAppModuleExtension = _project.extensions
                .getByName("android") as BaseAppModuleExtension

        val sourceSet = variant.sourceSets.find { it.name == variant.name } ?: return

        android.sourceSets.forEach { source ->
            if (source.name == sourceSet.name) {
                source.java.srcDir(_project.navJavaDir(variant))
                source.res.srcDir(_project.navResDir(variant))
            }
        }

        val task = _project.tasks.create(mapOf(
                "name" to variant.NavigationTaskName,
                "group" to "auto-param",
                "type" to NavigationTask::class.java
        ), closureOf<NavigationTask> {
            val files = mutableListOf<File>()
            var lastModified = -1L
            variant.sourceSets.forEach { sourceSet ->
                sourceSet.resDirectories.forEach { res ->
                    project.fileTree(File(res, "navigation")).forEach { file ->
                        files.add(file)
                        lastModified = file.lastModified().coerceAtLeast(lastModified)
                    }
                }
            }
            inputs.property("lastModified",lastModified)
            inputs.files(files.toTypedArray())
            resDir = project.file(project.navResDir(variant))
            javaDir = project.file(project.navJavaDir(variant))
            ruleFile = project.file(project.navRuleFile(variant))
        })

        val preBuild = "pre${variant.name.capitalize()}Build"
        _project.tasks.findByName(preBuild)?.dependsOn(task)
    }

    private fun registerPostProcessProguardRules(variant: ApplicationVariant, _project: Project) {
        variant.outputs.forEach { output ->
            val taskName = "process${variant.name.capitalize()}Resources"
            val task = _project.tasks.findByName(taskName)

            task?.let {
                output.processResourcesProvider.orNull?.doLast {
                    editAaptRule(project, variant)
                }
            }
        }
    }

}


