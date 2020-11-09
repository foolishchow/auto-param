package me.foolishchow.android.multimodule.dependence;

import org.gradle.api.Project;

/**
 * Description:
 * Author: foolishchow
 * Date: 20/10/2020 6:09 PM
 */
public class DefaultDependenceExtension implements IDependenceExtension{

    Project project;

    public DefaultDependenceExtension(Project project) {
        this.project = project;
    }

    @Override
    public void junit(String version) {

    }

    @Override
    public void androidxTestRunner(String version) {

    }
}
