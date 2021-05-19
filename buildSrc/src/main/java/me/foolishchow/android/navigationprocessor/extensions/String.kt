package me.foolishchow.android.navigationprocessor.extensions

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 3:12 PM
 */


val camelPattern = Pattern.compile("[A-Z]")
val snakePattern = Pattern.compile("_[a-z]")


@Suppress("DefaultLocale")
@JvmOverloads
fun String.camel2snake(uppercase: Boolean = false): String {
    val str = this
    val matcher: Matcher = camelPattern.matcher(str)
    val sb = StringBuffer()
    while (matcher.find()) {
        matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase())
    }
    matcher.appendTail(sb)
    return if (uppercase) {
        sb.toString().toUpperCase()
    } else sb.toString()
}


@Suppress("DefaultLocale")
fun String.snake2camel(): String {
    val str: String = this
    val matcher: Matcher = snakePattern.matcher(str);
    val sb = StringBuffer();
    while (matcher.find()) {
        matcher.appendReplacement(sb, matcher.group(0).substring(1).toUpperCase());
    }
    matcher.appendTail(sb)
    sb.setCharAt(0, sb[0].toUpperCase())
    return sb.toString();
}