package me.foolishchow.android.plugin.navigation

import com.android.build.gradle.api.ApplicationVariant
import me.foolishchow.android.navigationprocessor.extensions.AaptRules
import me.foolishchow.android.navigationprocessor.extensions.navRuleFile
import org.gradle.api.Project
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/21 10:53 AM
 */
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


fun editAaptRule(project: Project, variant: ApplicationVariant) {
    val rulesPath = project.AaptRules(variant)
    val rulesWrapper = Rules()


    //region 读取 aapt_rules.txt
    val ruleContent = mutableListOf<String>()

    val stream = BufferedReader(FileReader(rulesPath))
    var str: String?
    var line = 1
    while (stream.readLine().also { str = it } != null) {
        rulesWrapper.add(str, line)
        ruleContent.add(str ?: "")
        line++
    }
    rulesWrapper.add(str, line)

    val rules = rulesWrapper.rules
    //endregion


    //region 读取 navigation/${variant.name}/navigation.txt
    val navNameFile = project.navRuleFile(variant)
    val navFiles = mutableListOf<String>()
    val reader = BufferedReader(FileReader(navNameFile))
    while (reader.readLine().also { str = it } != null) {
        str?.let { navFiles.add(it) }
    }
    //endregion


    //region 获取需要被注释的line
    rules.forEach { rule ->
        val ref = rule.references.filter { it ->
            var has = false
            for (navFile in navFiles) {
                if (it.startsWith("# Referenced at $navFile")) {
                    has = true
                    break
                }
            }
            !has
        }
        rule.references.clear()
        rule.references.addAll(ref)
    }

    val needToCommentLines = rules.map(fun(it: Rule): Int? {
        if (it.className != null && it.references.size == 0) return it.line
        return null
    }).filterNotNull()
    //endregion

    val fw = FileWriter(rulesPath)
    val bw = BufferedWriter(fw)
    ruleContent.forEachIndexed { index, lineContent ->
        if (needToCommentLines.contains(index + 1)) {
            bw.write("# $lineContent")
        } else {
            bw.write(lineContent)
        }
        bw.newLine()
    }
    bw.close()

}