// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    alias(libs.plugins.android.library) apply false
}
buildscript {
    repositories{
        maven {
            url = uri("$rootDir/..")
        }
    }
    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.hilt.gradle)
        classpath(libs.androidx.nav.gradle)
    }
}

subprojects {
    parent!!.path.takeIf { it != rootProject.path }?.let { evaluationDependsOn(it) }
}

tasks.register("clean", Delete::class.java) {
    delete(project.layout.buildDirectory)
}

