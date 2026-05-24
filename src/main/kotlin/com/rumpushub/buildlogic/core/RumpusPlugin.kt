package com.rumpushub.buildlogic.core

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.api.plugins.JavaApplication
import com.rumpushub.buildlogic.tasks.RumpusTasks
import com.rumpushub.buildlogic.conventions.RumpusJavaConventionsPlugin

/**
 * Main Gradle plugin for the Rumpus project.
 *
 * Responsibilities:
 * - Applies common internal and external plugins
 * - Configures the application plugin
 * - Sets group and version for all modules
 */
class RumpusPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // --------------------------
        // Apply core Gradle plugins
        // --------------------------
        project.pluginManager.apply("visual-studio")
        project.pluginManager.apply("application")

        // --------------------------
        // Apply internal Rumpus plugins
        // --------------------------
        project.pluginManager.apply(RumpusJavaConventionsPlugin::class.java)
        project.pluginManager.apply(RumpusTasks::class.java)

        // --------------------------
        // Configure the application plugin (modern Gradle 7+)
        // --------------------------
        project.extensions.findByType(JavaApplication::class.java)?.apply {
            mainClass.set("com.rumpus.App")
        }
    }
}
