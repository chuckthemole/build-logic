package com.rumpushub.buildlogic.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project

class RumpusDependenciesPlugin : Plugin<Project> {

    open class RumpusDepsExtension {
        val core = mutableListOf<Any>()
        val web = mutableListOf<Any>()
        val db = mutableListOf<Any>()
        val security = mutableListOf<Any>()
        val cloud = mutableListOf<Any>()
        val devTools = mutableListOf<Any>()
        val testing = mutableListOf<Any>()

        // Extra bucket for miscellaneous dependencies
        val additionalDeps = mutableListOf<Any>()
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("rumpusDeps", RumpusDepsExtension::class.java)

        project.afterEvaluate {
            fun addDeps(deps: List<Any>, configuration: String = "implementation") {
                deps.forEach { dep ->
                    try {
                        project.dependencies.add(configuration, dep)
                        project.logger.lifecycle("Added $dep to $configuration")
                    } catch (e: Exception) {
                        project.logger.error("Failed to add dependency $dep to $configuration: ${e.message}")
                    }
                }
            }

            project.logger.lifecycle("Applying RumpusDependenciesPlugin dependencies...")

            addDeps(extension.core)
            addDeps(extension.web)
            addDeps(extension.db)
            addDeps(extension.security)
            addDeps(extension.cloud)
            addDeps(extension.devTools)
            addDeps(extension.testing, "testImplementation")
            addDeps(extension.additionalDeps)

            project.logger.lifecycle("RumpusDependenciesPlugin applied successfully")
        }
    }
}
