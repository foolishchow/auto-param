package me.foolishchow.android.plugin.navigation.extensions

import groovy.util.Node
import groovy.xml.QName
import java.io.File


const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
const val APP_NAMESPACE = "http://schemas.android.com/apk/res-auto"

fun Node.loopAttribute(predicate: (Map.Entry<QName, String>) -> Unit) {
    val attributes = this.attributes() as Map<QName, String>
    attributes.forEach(predicate)
}

/**
 * `app:destination`
 */
val QName.isAppDestination:Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("destination")
    }

/**
 * `android:label`
 */
val QName.isAndroidLabel: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("label")
    }

/**
 * `android:name`
 */
val QName.isAndroidName: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("name")
    }

/**
 * `android:id`
 */
val QName.isResId: Boolean
    get() {
        return namespaceURI.equals(ANDROID_NAMESPACE) && localPart.equals("id")
    }

/**
 * `app:startDestination`
 */
val QName.isStartDestination: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("startDestination")
    }

/**
 * `app:popEnterAnim`
 */
val QName.isPopEnterAnim: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("popEnterAnim")
    }

/**
 * `app:popExitAnim`
 */
val QName.isPopExitAnim: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("popExitAnim")
    }

/**
 * `app:enterAnim`
 */
val QName.isEnterAnim: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("enterAnim")
    }

/**
 * `app:exitAnim`
 */
val QName.isExitAnim: Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("exitAnim")
    }

val QName.isLaunchSingleTop:Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("launchSingleTop")
    }

val QName.isPopUpTo:Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("popUpTo")
    }

val QName.isPopUpToInclusive:Boolean
    get() {
        return namespaceURI.equals(APP_NAMESPACE) && localPart.equals("popUpToInclusive")
    }


val File.xmlName:String
    get() {
        return this.name.replace(".xml", "")
    }

