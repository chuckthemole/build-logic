package com.rumpushub.buildlogic.services

import com.rumpushub.buildlogic.conventions.RumpusTestConventions
import com.rumpushub.buildlogic.core.RumpusPlugin
import com.rumpushub.buildlogic.dependencies.AwsDependenciesPlugin
import com.rumpushub.buildlogic.dependencies.CommonSessionDependencies
import com.rumpushub.buildlogic.dependencies.OpenApiDependenciesPlugin
import com.rumpushub.buildlogic.dependencies.RumpusDependenciesPlugin
import com.rumpushub.buildlogic.testing.RumpusTest
import com.rumpushub.buildlogic.utils.EnvLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.springframework.boot.gradle.tasks.run.BootRun

/**
 * RumpusApplicationPlugin
 *
 * High-level composition plugin for the main Rumpus application.
 *
 * Responsibilities:
 * - Applies shared conventions/plugins
 * - Configures dependency buckets
 * - Configures repositories + environment loading
 * - Configures BootRun JVM/runtime behavior
 * - Handles DEV vs published common dependency resolution
 *
 * This plugin is intentionally orchestration-only.
 */
class RumpusApplicationPlugin : Plugin<Project> {

    // -------------------------------------------------------------------------
    // Version catalog helpers
    // -------------------------------------------------------------------------
    private fun Project.libs(): VersionCatalog =
        extensions
            .getByType(VersionCatalogsExtension::class.java)
            .named("rumpusLibs")

    private fun VersionCatalog.lib(name: String) =
        findLibrary(name).orElseThrow {
            IllegalArgumentException(
                "Library alias '$name' not found in rumpusLibs"
            )
        }

    override fun apply(project: Project) {

        val libs = project.libs()

        // ---------------------------------------------------------------------
        // Core conventions
        // ---------------------------------------------------------------------
        project.pluginManager.apply(RumpusPlugin::class.java)

        // ---------------------------------------------------------------------
        // Feature plugins
        // ---------------------------------------------------------------------
        project.pluginManager.apply(AwsDependenciesPlugin::class.java)
        project.pluginManager.apply(CommonSessionDependencies::class.java)
        project.pluginManager.apply(RumpusTest::class.java)
        project.pluginManager.apply(RumpusTestConventions::class.java)
        project.pluginManager.apply(OpenApiDependenciesPlugin::class.java)
        project.pluginManager.apply(RumpusDependenciesPlugin::class.java)

        // ---------------------------------------------------------------------
        // AWS
        // ---------------------------------------------------------------------
        project.extensions.configure(
            AwsDependenciesPlugin.AwsExtension::class.java
        ) {
            awsCoreDependency =
                libs.lib("springCloudAws").get()

            awsS3Dependency =
                libs.lib("springCloudAwsS3").get()
        }

        // ---------------------------------------------------------------------
        // Session
        // ---------------------------------------------------------------------
        project.extensions.configure(
            CommonSessionDependencies.SessionExtension::class.java
        ) {
            core.set(
                libs.findLibrary("springSessionCore")
                    .orElseThrow {
                        IllegalArgumentException(
                            "Missing springSessionCore"
                        )
                    }
            )

            jdbc.set(
                libs.findLibrary("springSessionJdbc")
                    .orElseThrow {
                        IllegalArgumentException(
                            "Missing springSessionJdbc"
                        )
                    }
            )
        }

        // ---------------------------------------------------------------------
        // Testing
        // ---------------------------------------------------------------------
        project.extensions.configure(
            RumpusTest.TestExtension::class.java
        ) {
            springBoot =
                libs.lib("springBootStarterTest").get()

            mockito =
                libs.lib("mockito").get()

            junitApi =
                libs.lib("junit").get()

            junitEngine =
                libs.lib("junitEngine").get()

            springSecurityTest =
                libs.lib("springSecurityTest").get()
        }

        project.extensions.configure(
            RumpusTestConventions.TestConventionsExtension::class.java
        ) {
            junitVersion =
                libs.lib("junit4").get()

            showStandardStreams = true
        }

        // ---------------------------------------------------------------------
        // OpenAPI
        // ---------------------------------------------------------------------
        project.extensions.configure(
            OpenApiDependenciesPlugin.OpenApiExtension::class.java
        ) {
            springdocUi =
                libs.lib("openApiUi").get()
        }

        // ---------------------------------------------------------------------
        // Aggregated dependency buckets
        // ---------------------------------------------------------------------
        project.extensions.configure(
            RumpusDependenciesPlugin.RumpusDepsExtension::class.java
        ) {

            core.addAll(
                listOf(
                    libs.lib("rumpusSpringBoot").get(),
                    libs.lib("springBootWeb").get()
                )
            )

            web.addAll(
                listOf(
                    libs.lib("webFlux").get(),
                    libs.lib("webSocket").get()
                )
            )

            db.addAll(
                listOf(
                    libs.lib("jpa").get(),
                    libs.lib("jdbc").get(),
                    libs.lib("mysql").get()
                )
            )

            security.addAll(
                listOf(
                    libs.lib("springSecurity").get(),
                    libs.lib("oauth2Client").get(),
                    libs.lib("jjwtApi").get(),
                    libs.lib("jjwtImpl").get(),
                    libs.lib("jjwtJackson").get()
                )
            )

            cloud.addAll(
                listOf(
                    libs.lib("springCloudAws").get(),
                    libs.lib("springCloudAwsS3").get()
                )
            )

            devTools.addAll(
                listOf(
                    libs.lib("devTools").get()
                )
            )

            testing.addAll(
                listOf(
                    libs.lib("junit").get(),
                    libs.lib("mockito").get()
                )
            )

            additionalDeps.addAll(
                listOf(
                    libs.lib("springBootActuator").get(),
                    libs.lib("springBootAdminClient").get(),
                    libs.lib("springBootAdminServer").get(),
                    libs.lib("commonsValidator").get(),
                    libs.lib("bootstrap").get(),
                    libs.lib("htmlunit").get(),
                    libs.lib("unirest").get(),
                    libs.lib("jsr305").get(),
                    libs.lib("j2html").get(),
                    libs.lib("jython").get(),
                    libs.lib("tess4j").get(),
                    libs.lib("oauth2ResourceServer").get()
                )
            )
        }

        // ---------------------------------------------------------------------
        // Project metadata
        // ---------------------------------------------------------------------
        project.group =
            libs.findVersion("rumpusGroup")
                .get()
                .requiredVersion

        project.version =
            libs.findVersion("rumpus")
                .get()
                .requiredVersion

        // ---------------------------------------------------------------------
        // Environment
        // ---------------------------------------------------------------------
        EnvLoader.loadDotEnv(project)

        val env =
            project.findProperty("ENV") as? String ?: "DEV"

        val heap =
            project.findProperty("HEAP") as? String
                ?: "LIMITED_HEAP"

        project.logger.lifecycle(
            "RumpusApplicationPlugin using environment: $env"
        )

        project.logger.lifecycle(
            "RumpusApplicationPlugin using heap profile: $heap"
        )

        // ---------------------------------------------------------------------
        // Spring Boot plugins
        // ---------------------------------------------------------------------
        project.pluginManager.apply("org.springframework.boot")
        project.pluginManager.apply("io.spring.dependency-management")

        // ---------------------------------------------------------------------
        // Common dependency handling
        // ---------------------------------------------------------------------
        project.dependencies.add(
            "implementation",
            if (env == "DEV") {

                project.logger.lifecycle(
                    "Using local :common dependency"
                )

                project.project(":common")

            } else {

                project.logger.lifecycle(
                    "Using published common artifact"
                )

                libs.lib("common").get()
            }
        )

        // ---------------------------------------------------------------------
        // BootRun configuration
        // ---------------------------------------------------------------------
        project.tasks.named(
            "bootRun",
            BootRun::class.java
        ) {

            systemProperty(
                "env",
                if (env == "DEV") "dev" else "live"
            )

            if (heap == "LIMITED_HEAP") {

                jvmArgs(
                    "-Xmx512m",
                    "-Xms256m"
                )
            }
        }
    }
}