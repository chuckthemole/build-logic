package com.rumpushub.buildlogic.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies library-related conventions for Rumpus modules.
 *
 * This plugin sets up:
 * - Java library plugin
 * - Maven publishing
 * - Shared internal Java conventions
 *
 * Any module that is a library should apply this plugin
 * to automatically get consistent build conventions.
 */
class RumpusLibraryConventions : Plugin<Project> {
    override fun apply(project: Project) {
        // --------------------------
        // Core Gradle plugins for libraries
        // --------------------------
        project.pluginManager.apply("java-library")    // Java library support
        project.pluginManager.apply("maven-publish")   // Publishing support

        // --------------------------
        // Apply internal Java conventions
        // --------------------------
        project.pluginManager.apply(RumpusJavaConventionsPlugin::class.java)
    }
}
