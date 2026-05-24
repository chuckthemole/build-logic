package com.rumpushub.buildlogic.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.rumpushub.buildlogic.tasks.RumpusTasks

/**
 * Applies common Rumpus application plugins.
 *
 * This plugin centralizes:
 * - Core Gradle plugins (application, Visual Studio support, publishing)
 * - Java conventions
 * - Custom internal plugins for AWS, database, session management, tasks, and testing
 *
 * Use this plugin on any module that needs the full set of standard Rumpus conventions.
 */
class CommonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // --------------------------
        // Apply core Gradle plugins
        // --------------------------
        project.pluginManager.apply("visual-studio")   // Optional: Visual Studio project support
        project.pluginManager.apply("application")     // Standard Gradle application plugin

        // --------------------------
        // Apply internal Java conventions
        // --------------------------
        project.pluginManager.apply(RumpusJavaConventionsPlugin::class.java)

        // --------------------------
        // Apply custom Rumpus modules
        // --------------------------
        project.pluginManager.apply(RumpusTasks::class.java)

        // --------------------------
        // Apply publishing plugin
        // --------------------------
        project.pluginManager.apply("maven-publish")   // Enables publishing artifacts
    }
}
