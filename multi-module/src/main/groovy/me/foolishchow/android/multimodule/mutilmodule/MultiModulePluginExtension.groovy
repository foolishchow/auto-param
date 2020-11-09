package me.foolishchow.android.multimodule.mutilmodule


import org.gradle.api.Project

class MultiModulePluginExtension implements  IMultiModulePluginExtension{

    Set<String> projectSet = new HashSet<>();
    Project project
    OnModuleIncludeListener mListener;

    MultiModulePluginExtension(Project project) {
        this.project = project
    }

    @Override
    void include(String projectName) {
        projectSet.add(projectName)
        if(mListener != null){
            mListener.addIncludeModule(projectName);
        }
    }


    @Override
    Set<String> getProjects() {
        return projectSet
    }

    @Override
    void addModuleIncludeListener(OnModuleIncludeListener listener) {
        mListener = listener;
    }
}