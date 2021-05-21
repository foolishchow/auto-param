package me.foolishchow.android.plugin.navigation.extensions

import java.util.regex.Pattern

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 7:07 PM
 */
val String.resId: String
    get() {
        return this.replace("+", "").replace("@id/", "")
    }


val IdPattern = Pattern.compile("\\@\\+?id\\/")
val AnimPattern = Pattern.compile("@anim\\/")

val String.resourceSymbol: String
    get() {
        var matcher = IdPattern.matcher(this)
        if (matcher.find()) {
            return this.replace("+", "").replace("@id/", "\$T.id.")
        }

        matcher = AnimPattern.matcher(this)
        if (matcher.find()) {
            return this.replace("+", "").replace("@anim/", "\$T.anim.")
        }

        if (equals("true") || equals("false")) {
            return this
        }

        return "\"${this}\""
    }