package com.rumpushub.buildlogic.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * RumpusTestConventions
 *
 * Configures testing for Rumpus projects.
 * Supports JUnit version configuration and test logging options.
 *
 * Example usage in build.gradle.kts:
 * ```
 * apply<RumpusTestConventions>()
 *
 * testConventions {
 *     junitVersion = rumpusLibs.junit
 *     showStandardStreams = true
 * }
 * ```
 */
class RumpusTestConventions : Plugin<Project> {

    open class TestConventionsExtension {
        var junitVersion: Any? = null
        var showStandardStreams: Boolean = true
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("testConventions", TestConventionsExtension::class.java)

        project.afterEvaluate {
            val junitDep = extension.junitVersion ?: throw IllegalArgumentException(
                "RumpusTestConventions requires 'junitVersion' to be set (e.g., from version catalog)."
            )

            // Add JUnit dependency
            project.dependencies {
                add("testImplementation", junitDep)
            }

            // Configure the "test" task to use JUnit Platform
            project.tasks.named("test", Test::class.java) {
                useJUnitPlatform()
            }

            // Configure logging for all Test tasks
            project.tasks.withType<Test> {
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    events("started", "skipped", "passed", "failed")
                    showStandardStreams = extension.showStandardStreams
                }
            }

            project.logger.lifecycle("RumpusTestConventions applied with JUnit: $junitDep, showStandardStreams: ${extension.showStandardStreams}")
        }
    }
}
