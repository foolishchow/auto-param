plugins {
    `kotlin-dsl`
    `maven`
    `java`
}

repositories {
    google()
    jcenter()
}

java{
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.squareup:javapoet:1.11.1")
    implementation("com.android.tools.build:gradle:3.6.3")
}

group = "com.github.foolishchow"