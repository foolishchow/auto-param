package me.foolishchow.android.navigationtransform

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

public class NavigationTransformer implements Plugin<Project> {

    private FileCollection collect
    private final List<String> res = new ArrayList<>();
    @Override
    void apply(Project project) {
        BaseAppModuleExtension android = project.extensions.getByType(AppExtension)

        android.buildTypes.each {buildType->
            android.sourceSets["main"].getJava().srcDir("${project.buildDir}/generated/navigation/${buildType.name}/out")
            project.tasks.create(
                    name: "navigationTransformTask${buildType.name}",
                    group: 'auto-param',
                    type: NavigationTransformTask
            ){
                def files = [];
                android.sourceSets.each { sourceSet ->
                    sourceSet.res.each { res ->
                        res.srcDirs.each { dir ->
                            project.fileTree("$dir/navigation").each { file ->
                                files.add(file)
                            }
                        }
                    }
                }
                inputs.property("buildType",buildType.name)
                inputs.files(files.toArray())
            }
        }
    }
}