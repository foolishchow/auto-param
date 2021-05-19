package me.foolishchow.android.navigationprocessor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/19 2:26 PM
 */

val Navigator = ParameterizedTypeName.get(
        ClassName.get("androidx.navigation", "Navigator"),
        WildcardTypeName.subtypeOf(TypeName.OBJECT)
)

val NavAction = ClassName.get(
        "androidx.navigation",
        "NavAction"
)


val NavigationUtil = ClassName.get("me.foolishchow.androidplugins.fake", "NavigationUtils")
val NavigatorProvider = ClassName.get("androidx.navigation", "NavigatorProvider")


val NavController = ClassName.get(
        "androidx.navigation",
        "NavController")

val FragmentNavigator = ClassName.get(
        "androidx.navigation.fragment",
        "FragmentNavigator"
)
val FragmentDestination = ClassName.get(
        "androidx.navigation.fragment",
        "FragmentNavigator.Destination"
)

val NavGraphNavigator = ClassName.get(
        "androidx.navigation",
        "NavGraphNavigator"
)
val NavGraph = ClassName.get(
        "androidx.navigation",
        "NavGraph"
)

val NavOptionsBuilder = ClassName.get(
        "androidx.navigation",
        "NavOptions.Builder"
)






