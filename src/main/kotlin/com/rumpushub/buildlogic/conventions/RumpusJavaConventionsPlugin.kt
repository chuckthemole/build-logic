package com.rumpushub.buildlogic.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * Applies Java conventions across Rumpus projects.
 */
class RumpusJavaConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Apply Java library plugin
        project.plugins.apply("java-library")

        // Configure Java compiler options
        project.tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        }
    }
}
