package com.rumpushub.buildlogic.services

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin

import org.gradle.api.plugins.JavaPlugin

class SpringBootServicePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.pluginManager.apply("org.springframework.boot")
        project.pluginManager.apply("io.spring.dependency-management")

        // wait for Spring Boot plugin lifecycle completion
        project.pluginManager.withPlugin("org.springframework.boot") {

            project.afterEvaluate {

                if (project.configurations.findByName("developmentOnly") != null) {
                    project.dependencies.add(
                        "developmentOnly",
                        "org.springframework.boot:spring-boot-devtools"
                    )
                } else {
                    project.logger.warn(
                        "developmentOnly configuration not found; skipping devtools"
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // Optional: disable devtools in production builds
        // ---------------------------------------------------------------------
        project.extensions.extraProperties.set(
            "spring.devtools.restart.enabled",
            true
        )
    }
}