package com.rumpushub.buildlogic.dependencies

import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.api.artifacts.MinimalExternalModuleDependency

/**
 * CommonSessionDependencies
 *
 * Adds dependencies for Spring Session.
 * Supports core and JDBC session modules.
 *
 * Features:
 * - Centralized version management via version catalog or explicit assignment.
 * - Type-safe Kotlin DSL configuration through a Gradle extension.
 * - Lazy Gradle Property API support (configuration-cache friendly).
 * - Clear error messages if required versions/dependencies are not provided.
 *
 * Example usage in build.gradle.kts:
 * ```
 * plugins {
 *     id("com.rumpushub.session-dependencies")
 * }
 *
 * sessionDeps {
 *     core.set(rumpusLibs.springSessionCore)
 *     jdbc.set(rumpusLibs.springSessionJdbc)
 * }
 * ```
 *
 * Why use Property<T> instead of mutable vars?
 * ---------------------------------------------
 * Modern Gradle strongly prefers lazy configuration APIs because they:
 *
 * - Improve configuration cache compatibility
 * - Avoid eager object realization
 * - Improve build performance
 * - Enable safer task graph construction
 * - Integrate better with Providers and Version Catalogs
 *
 * This plugin intentionally avoids `afterEvaluate` and uses Gradle's
 * lazy configuration model instead.
 */
class CommonSessionDependencies : Plugin<Project> {

    /**
     * Extension exposed to consuming build scripts.
     *
     * Example:
     * ```
     * sessionDeps {
     *     core.set(rumpusLibs.springSessionCore)
     *     jdbc.set(rumpusLibs.springSessionJdbc)
     * }
     * ```
     */
    abstract class SessionExtension @Inject constructor(
        objects: ObjectFactory
    ) {

        /**
         * Spring Session core dependency coordinate/provider.
         */
        abstract val core: Property<MinimalExternalModuleDependency>

        /**
         * Spring Session JDBC dependency coordinate/provider.
         */
        abstract val jdbc: Property<MinimalExternalModuleDependency>
    }

    override fun apply(project: Project) {

        // ---------------------------------------------------------------------
        // Register extension
        // ---------------------------------------------------------------------
        val extension = project.extensions.create<SessionExtension>(
            "sessionDeps"
        )

        project.dependencies.addProvider("implementation", extension.core)
        project.dependencies.addProvider("implementation", extension.jdbc)
        
        // ---------------------------------------------------------------------
        // Configure dependencies after project evaluation
        // ---------------------------------------------------------------------
        // project.afterEvaluate {

        //     val coreDep = extension.core.orNull
        //         ?: throw IllegalArgumentException(
        //             "CommonSessionDependencies requires " +
        //                     "'core' to be set " +
        //                     "(e.g., from version catalog)."
        //         )

        //     val jdbcDep = extension.jdbc.orNull
        //         ?: throw IllegalArgumentException(
        //             "CommonSessionDependencies requires " +
        //                     "'jdbc' to be set " +
        //                     "(e.g., from version catalog)."
        //         )

        //     project.dependencies.add(
        //         "implementation",
        //         coreDep
        //     )

        //     project.dependencies.add(
        //         "implementation",
        //         jdbcDep
        //     )

        //     project.logger.lifecycle(
        //         "CommonSessionDependencies applied: " +
        //                 "$coreDep, $jdbcDep"
        //     )
        // }
    }
}