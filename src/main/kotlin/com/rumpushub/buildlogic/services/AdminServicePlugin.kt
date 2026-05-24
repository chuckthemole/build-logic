package com.rumpushub.buildlogic.services

import com.rumpushub.buildlogic.conventions.CommonPlugin
import com.rumpushub.buildlogic.conventions.RumpusTestConventions
import com.rumpushub.buildlogic.core.RumpusPlugin
import com.rumpushub.buildlogic.dependencies.*
import com.rumpushub.buildlogic.testing.RumpusTest
import com.rumpushub.buildlogic.utils.EnvLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

/**
 * AdminServicePlugin
 *
 * High-level composition plugin for the Admin service module.
 *
 * This plugin:
 * - Applies shared conventions and feature plugins
 * - Centralizes dependency composition for the Admin service
 * - Configures AWS / DB / Session / Testing dependencies
 * - Loads environment configuration (DEV / BETA / LIVE)
 * - Configures repositories dynamically
 *
 * IMPORTANT:
 * This is a "composition layer" plugin only.
 * It should NOT contain business logic or module-specific code.
 */
class AdminServicePlugin : Plugin<Project> {

    // -------------------------------------------------------------------------
    // Version Catalog Access (safe, explicit, build-logic compatible)
    // -------------------------------------------------------------------------
    private fun Project.libs(): VersionCatalog = extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("rumpusLibs")

    private fun VersionCatalog.lib(name: String) =
        findLibrary(name).orElseThrow {
            IllegalArgumentException("Library alias '$name' not found in rumpusLibs catalog")
        }

    override fun apply(project: Project) {

        val libs = project.libs()

        // ---------------------------------------------------------------------
        // Core build conventions
        // ---------------------------------------------------------------------
        project.pluginManager.apply(CommonPlugin::class.java)
        project.pluginManager.apply(RumpusPlugin::class.java)

        // ---------------------------------------------------------------------
        // Feature plugins
        // ---------------------------------------------------------------------
        project.pluginManager.apply(AwsDependenciesPlugin::class.java)
        project.pluginManager.apply(CommonDBDependenciesPlugin::class.java)
        project.pluginManager.apply(CommonSessionDependencies::class.java)
        project.pluginManager.apply(RumpusTest::class.java)
        project.pluginManager.apply(RumpusTestConventions::class.java)
        project.pluginManager.apply(RumpusDependenciesPlugin::class.java)

        // ---------------------------------------------------------------------
        // AWS
        // ---------------------------------------------------------------------
        project.extensions.configure(AwsDependenciesPlugin.AwsExtension::class.java) {
            awsCoreDependency = libs.lib("springCloudAws").get()
            awsS3Dependency = libs.lib("springCloudAwsS3").get()
        }

        // ---------------------------------------------------------------------
        // DB
        // ---------------------------------------------------------------------
        project.extensions.configure(CommonDBDependenciesPlugin.DbExtension::class.java) {
            springJdbc = libs.lib("springJdbc").get()
            springDataJpa = libs.lib("springDataJpa").get()
            mysqlConnector = libs.lib("mysql").get()
            redis = libs.lib("springDataRedis").get()
            jedis = libs.lib("jedis").get()
            jooq = libs.lib("jooq").get()
        }

        // ---------------------------------------------------------------------
        // Session
        // ---------------------------------------------------------------------
        project.extensions.configure(CommonSessionDependencies.SessionExtension::class.java) {
            core.set(
                libs.findLibrary("springSessionCore")
                    .orElseThrow { IllegalArgumentException("Missing springSessionCore") }
            )

            jdbc.set(
                libs.findLibrary("springSessionJdbc")
                    .orElseThrow { IllegalArgumentException("Missing springSessionJdbc") }
            )
        }

        // ---------------------------------------------------------------------
        // Testing
        // ---------------------------------------------------------------------
        project.extensions.configure(RumpusTest.TestExtension::class.java) {
            springBoot = libs.lib("springBootStarterTest").get()
            mockito = libs.lib("mockito").get()
            junitApi = libs.lib("junit").get()
            junitEngine = libs.lib("junitEngine").get()
            springSecurityTest = libs.lib("springSecurityTest").get()
        }

        project.extensions.configure(RumpusTestConventions.TestConventionsExtension::class.java) {
            junitVersion = libs.lib("junit4").get()
            showStandardStreams = true
        }

        // ---------------------------------------------------------------------
        // Aggregated dependency buckets
        // ---------------------------------------------------------------------
        project.extensions.configure(RumpusDependenciesPlugin.RumpusDepsExtension::class.java) {

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
        // Environment
        // ---------------------------------------------------------------------
        EnvLoader.loadDotEnv(project)

        val env = project.findProperty("ENV") as? String ?: "DEV"
        project.logger.lifecycle("AdminServicePlugin using environment: $env")

        // ---------------------------------------------------------------------
        // Repositories
        // ---------------------------------------------------------------------
        project.repositories.apply {
            gradlePluginPortal()
            mavenCentral()

            when (env) {
                "LIVE" -> maven {
                    url = project.uri("https://maven.pkg.github.com/chuckthemole/common")
                    credentials {
                        username = project.findProperty("GPR_USER") as? String
                            ?: System.getenv("GPR_USER")
                        password = project.findProperty("GPR_TOKEN") as? String
                            ?: System.getenv("GPR_TOKEN")
                    }
                }

                "BETA" -> maven {
                    url = project.uri("${project.rootDir}/TestRepo")
                }

                "DEV" -> mavenLocal()
            }
        }

        // ---------------------------------------------------------------------
        // Spring Boot (plugin IDs remain string-based)
        // ---------------------------------------------------------------------
        project.pluginManager.apply("org.springframework.boot")
        project.pluginManager.apply("io.spring.dependency-management")

        // ---------------------------------------------------------------------
        // Core dependency (DEV uses project, others use artifact)
        // ---------------------------------------------------------------------
        project.dependencies.add(
            "implementation",
            if (env == "DEV") {
                project.project(":common")
            } else {
                "com.rumpushub.common:common:0.1.3"
            }
        )
    }
}