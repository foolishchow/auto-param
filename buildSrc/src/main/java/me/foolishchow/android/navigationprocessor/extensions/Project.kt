package me.foolishchow.android.navigationprocessor.extensions

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BuildType
import org.gradle.api.Project

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/20 9:05 AM
 */
fun Project.navigationDir(buildType: BuildType): String {
    return "${project.buildDir}/generated/source/navigation/${buildType.name}"
}

fun Project.navigationDir(variant: ApplicationVariant): String {
    return "${project.buildDir}/generated/source/navigation/${variant.name}"
}

fun Project.AaptRules(variant: ApplicationVariant): String {
    return "${project.buildDir.absolutePath}/intermediates/aapt_proguard_file/${variant.name}/aapt_rules.txt"
}

val ApplicationVariant.NavigationTaskName: String
    get() {
        return "transfer${this.name.capitalize()}Navigation"
    }