package com.rumpushub.buildlogic.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * AwsDependenciesPlugin
 *
 * Adds AWS Spring Cloud dependencies to a project.
 * Intended for modules that require AWS S3 integration.
 *
 * Features:
 * - Centralized dependency management via version catalogs or explicit assignment.
 * - Type-safe Kotlin DSL configuration through a Gradle extension.
 * - Provides clear error messages if dependencies are not set.
 */
class AwsDependenciesPlugin : Plugin<Project> {

    /**
     * Extension exposed to the build script for configuration.
     *
     * Example usage in build.gradle.kts:
     * ```
     * apply<AwsDependenciesPlugin>()
     *
     * awsDeps {
     *     awsCoreDependency = rumpusLibs.springCloudAws
     *     awsS3Dependency = rumpusLibs.springCloudAwsS3
     * }
     * ```
     */
    open class AwsExtension {
        var awsCoreDependency: Any? = null
        var awsS3Dependency: Any? = null
    }

    override fun apply(project: Project) {
        // Create the extension
        val extension = project.extensions.create("awsDeps", AwsExtension::class.java)

        // Configure dependencies after project evaluation
        project.afterEvaluate {
            val coreDep = extension.awsCoreDependency
                ?: throw IllegalArgumentException(
                    "AwsDependenciesPlugin requires 'awsCoreDependency' to be set (e.g., from version catalog)."
                )

            val s3Dep = extension.awsS3Dependency
                ?: throw IllegalArgumentException(
                    "AwsDependenciesPlugin requires 'awsS3Dependency' to be set (e.g., from version catalog)."
                )

            // Add dependencies to the project implementation configuration
            project.dependencies.add("implementation", coreDep)
            project.dependencies.add("implementation", s3Dep)

            // Optional: log for debugging purposes
            project.logger.lifecycle("AwsDependenciesPlugin applied: $coreDep, $s3Dep")
        }
    }
}
