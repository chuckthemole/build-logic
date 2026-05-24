package com.rumpushub.buildlogic.formatting

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import javax.inject.Inject

/**
 * -----------------------------------------------------------------------------
 * SpotlessPlugin
 * -----------------------------------------------------------------------------
 *
 * Convention plugin that standardizes code formatting across all Rumpus modules.
 *
 * Responsibilities:
 *  - Applies Spotless plugin
 *  - Configures Java formatting via Eclipse formatter
 *  - Configures Kotlin + Kotlin Gradle formatting
 *  - Enables conditional Kotlin configuration when Kotlin plugins are present
 *
 * Design principles:
 *  - No hardcoded build logic exposed to users (only formatting inputs)
 *  - Deterministic formatting across all modules
 *  - Safe for multi-module Gradle builds
 *  - Compatible with buildSrc and convention plugin architecture
 * -----------------------------------------------------------------------------
 */
class SpotlessPlugin : Plugin<Project> {

    /**
     * -----------------------------------------------------------------------------
     * Extension (user-configurable surface)
     * -----------------------------------------------------------------------------
     */
    open class RumpusSpotlessExtension @Inject constructor() {

        var javaTarget: String = "**/*.java"

        var kotlinTarget: String = "src/**/*.kt"

        var kotlinGradleTargets: List<String> = listOf(
            "*.gradle.kts",
            "**/*.gradle.kts"
        )
    }

    override fun apply(project: Project) {

        // ---------------------------------------------------------------------
        // Apply Spotless plugin (always explicit, never configurable)
        // ---------------------------------------------------------------------
        project.pluginManager.apply("com.diffplug.spotless")

        // ---------------------------------------------------------------------
        // Create extension for build script configuration
        // ---------------------------------------------------------------------
        val ext = project.extensions.create(
            "spotlessConfig",
            RumpusSpotlessExtension::class.java
        )

        // ---------------------------------------------------------------------
        // Base configuration (Java + Kotlin Gradle DSL)
        // ---------------------------------------------------------------------
        project.extensions.configure<SpotlessExtension> {

            // ----------------------------
            // Java formatting
            // ----------------------------
            java {
                target(ext.javaTarget)

                eclipse().configFile(
                    project.rootProject.file(".vscode/java-formatter.xml")
                )

                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
            }

            // ----------------------------
            // Gradle Kotlin DSL formatting
            // ----------------------------
            kotlinGradle {
                target(*ext.kotlinGradleTargets.toTypedArray())

                ktfmt()

                trimTrailingWhitespace()
                endWithNewline()
            }
        }

        // ---------------------------------------------------------------------
        // Kotlin JVM support (only if plugin is applied)
        // ---------------------------------------------------------------------
        project.plugins.withId("org.jetbrains.kotlin.jvm") {

            project.extensions.configure<SpotlessExtension> {

                kotlin {
                    target(ext.kotlinTarget)

                    ktfmt()

                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }

        // ---------------------------------------------------------------------
        // Kotlin Android support (future-proof)
        // ---------------------------------------------------------------------
        project.plugins.withId("org.jetbrains.kotlin.android") {

            project.extensions.configure<SpotlessExtension> {

                kotlin {
                    target(ext.kotlinTarget)

                    ktfmt()

                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }
    }
}