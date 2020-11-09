package me.foolishchow.android.multimodule.mutilmodule

public interface IMultiModulePluginExtension {
    void include(String projectName);
    Set<String> getProjects();
    void addModuleIncludeListener(OnModuleIncludeListener listener)
}