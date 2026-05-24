package com.rumpushub.buildlogic.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * OpenApiDependenciesPlugin
 *
 * Adds SpringDoc OpenAPI dependencies to a project.
 * Intended for modules that need API documentation support via OpenAPI 3.
 *
 * Features:
 * - Centralized dependency management via version catalogs or explicit assignment.
 * - Developer can choose between API-only or full UI (Swagger) setup.
 * - Optional UI dependency for flexibility across backend and full-stack modules.
 */
class OpenApiDependenciesPlugin : Plugin<Project> {

    /**
     * Extension exposed to the build script for configuration.
     *
     * Example usage in build.gradle.kts:
     * ```
     * apply<OpenApiDependenciesPlugin>()
     *
     * openApiDeps {
     *     // Choose one or both
     *     springdocCore = rumpusLibs.openApiCore     // For API docs only
     *     springdocUi = rumpusLibs.openApiUi           // Optional Swagger UI
     * }
     * ```
     */
    open class OpenApiExtension {
        /** Dependency for the OpenAPI core (required for documentation generation). */
        var springdocCore: Any? = null

        /** Optional dependency for the Swagger UI frontend. */
        var springdocUi: Any? = null
    }

    override fun apply(project: Project) {
        // Create the extension
        val extension = project.extensions.create("openApiDeps", OpenApiExtension::class.java)

        // Configure dependencies after project evaluation
        project.afterEvaluate {
            val coreDep = extension.springdocCore
            val uiDep = extension.springdocUi

            if (coreDep == null && uiDep == null) {
                throw IllegalArgumentException(
                    """
                    OpenApiDependenciesPlugin requires at least one dependency:
                      - springdocCore (e.g., rumpusLibs.openApiCore) for API docs only
                      - springdocUi (e.g., rumpusLibs.openApiUi) for full Swagger UI support
                    """.trimIndent()
                )
            }

            coreDep?.let {
                project.dependencies.add("implementation", it)
                project.logger.lifecycle("Added SpringDoc OpenAPI Core dependency: $it")
            }

            uiDep?.let {
                project.dependencies.add("implementation", it)
                project.logger.lifecycle("Added optional SpringDoc OpenAPI UI dependency: $it")
            }

            when {
                coreDep != null && uiDep != null ->
                    project.logger.lifecycle("OpenAPI setup: Core + UI (Swagger) integrated successfully.")
                uiDep != null ->
                    project.logger.lifecycle("OpenAPI setup: UI only (includes Core internally).")
                else ->
                    project.logger.lifecycle("OpenAPI setup: Core only (no UI).")
            }
        }
    }
}
