package me.foolishchow.android.plugin.navigation.extensions

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BuildType
import org.gradle.api.Project

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/20 9:05 AM
 */

fun Project.navRuleFile(variant: ApplicationVariant):String{
    return "${project.buildDir}/intermediates/navigation/${variant.name}/navigation.txt"
}

fun Project.navJavaDir(variant: ApplicationVariant): String {
    return "${project.buildDir}/generated/source/navigation/${variant.name}/java"
}

fun Project.navResDir(variant: ApplicationVariant): String {
    return "${project.buildDir}/generated/source/navigation/${variant.name}/res"
}

fun Project.AaptRules(variant: ApplicationVariant): String {
    return "${project.buildDir.absolutePath}/intermediates/aapt_proguard_file/${variant.name}/aapt_rules.txt"
}

val ApplicationVariant.NavigationTaskName: String
    get() {
        return "transfer${this.name.capitalize()}Navigation"
    }