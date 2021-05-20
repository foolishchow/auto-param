package me.foolishchow.android.navigationprocessor

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import me.foolishchow.android.navigationprocessor.extensions.AaptRules
import me.foolishchow.android.navigationprocessor.extensions.NavigationTaskName
import me.foolishchow.android.navigationprocessor.extensions.navigationDir
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.closureOf
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

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
            postProcessProguardRules(this, project)
            registerNavigationTask(this, project)
        }
    }


    private fun registerNavigationTask(variant: ApplicationVariant, project: Project) {
        val android: BaseAppModuleExtension = project.extensions
                .getByName("android") as BaseAppModuleExtension

        val sourceSet = variant.sourceSets.find { it.name == variant.name } ?: return

        android.sourceSets.forEach { source ->
            if (source.name == sourceSet.name) {
                source.java.srcDir(project.navigationDir(variant))
            }
        }

        val task = project.tasks.create(mapOf(
                "name" to variant.NavigationTaskName,
                "group" to "auto-param",
                "type" to NavigationTask::class.java
        ), closureOf<NavigationTask> {
            val files = mutableListOf<File>()
            variant.sourceSets.forEach { sourceSet ->
                sourceSet.resDirectories.forEach { res ->
                    project.fileTree(File(res, "navigation")).forEach { file ->
                        files.add(file)
                    }
                }
            }
            this.inputs.property("variant", variant.name)
            this.inputs.files(files.toTypedArray())
            this.outputs.dir("${project.buildDir}/generated/source/navigation/${variant.name}")
                    .withPropertyName("outputDir")
        })

        val preBuild = "pre${variant.name.capitalize()}Build"
        project.tasks.findByName(preBuild)?.dependsOn(task)
    }

    private fun postProcessProguardRules(variant: ApplicationVariant, project: Project) {
        variant.outputs.forEach { output ->
            val taskName = "process${variant.name.capitalize()}Resources"
            val task = project.tasks.findByName(taskName)

            task?.let {
                output.processResourcesProvider.orNull?.doLast {
                    val rulesPath = project.AaptRules(variant)
                    val rules = Rules()

                    val stream = BufferedReader(FileReader(rulesPath))
                    var str: String?
                    var line = 0
                    while (stream.readLine().also { str = it } != null) {
                        rules.add(str, line)
                        line++
                    }
                    rules.add(str, line)
                    rules.rules.forEach { rule ->
                        println(rule.className)
                    }
                }
            }
        }
    }
}

class Rules {
    val rules = mutableListOf<Rule>()
    var rule = Rule()

    init {
        rules.add(rule)
    }

    fun add(str: String?, line: Int) {
        if (str.isNullOrBlank()) {
            rule = Rule()
            rules.add(rule)
        } else if (str.startsWith("# Referenced at")) {
            rule.references.add(str)
        } else if (str.startsWith("-keep class ")) {
            rule.className = str
            rule.line = line
        }
    }
}

class Rule {
    var line = -1
    var className: String? = null
    val references = mutableListOf<String>()

}
