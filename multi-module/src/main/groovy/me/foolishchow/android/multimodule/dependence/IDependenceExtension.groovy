package me.foolishchow.android.multimodule.dependence

interface IDependenceExtension{
    /**
     * junit junit:junit 配置
     * @param version
     */
    void junit(String version);

    /**
     * androidx.test:runner 版本
     * @param version
     */
    void androidxTestRunner(String version)
}


