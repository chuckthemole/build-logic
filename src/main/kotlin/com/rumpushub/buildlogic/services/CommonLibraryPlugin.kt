package com.rumpushub.buildlogic.services

import com.rumpushub.buildlogic.conventions.CommonPlugin
import com.rumpushub.buildlogic.conventions.RumpusTestConventions
import com.rumpushub.buildlogic.dependencies.AwsDependenciesPlugin
import com.rumpushub.buildlogic.dependencies.CommonDBDependenciesPlugin
import com.rumpushub.buildlogic.dependencies.CommonSessionDependencies
import com.rumpushub.buildlogic.dependencies.OpenApiDependenciesPlugin
import com.rumpushub.buildlogic.dependencies.RumpusDependenciesPlugin
import com.rumpushub.buildlogic.publishing.CommonPublisherPlugin
import com.rumpushub.buildlogic.testing.RumpusTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

/**
 * CommonLibraryPlugin
 *
 * High-level composition plugin for the shared `common` module.
 *
 * Responsibilities:
 * - Applies shared conventions
 * - Applies publishing configuration
 * - Configures shared dependency buckets
 * - Configures testing
 * - Configures OpenAPI support
 * - Produces a standard JAR artifact
 *
 * This plugin defines the build/runtime contract for the shared
 * reusable Rumpus library module.
 */
class CommonLibraryPlugin : Plugin<Project> {

    // -------------------------------------------------------------------------
    // Version Catalog Helpers
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
        project.pluginManager.apply(CommonPlugin::class.java)

        // ---------------------------------------------------------------------
        // Publishing
        // ---------------------------------------------------------------------
        project.pluginManager.apply(CommonPublisherPlugin::class.java)

        // ---------------------------------------------------------------------
        // Dependency plugins
        // ---------------------------------------------------------------------
        project.pluginManager.apply(AwsDependenciesPlugin::class.java)
        project.pluginManager.apply(CommonDBDependenciesPlugin::class.java)
        project.pluginManager.apply(CommonSessionDependencies::class.java)
        project.pluginManager.apply(RumpusTest::class.java)
        project.pluginManager.apply(RumpusTestConventions::class.java)
        project.pluginManager.apply(OpenApiDependenciesPlugin::class.java)
        project.pluginManager.apply(RumpusDependenciesPlugin::class.java)

        // ---------------------------------------------------------------------
        // AWS
        // ---------------------------------------------------------------------
        project.extensions.configure<AwsDependenciesPlugin.AwsExtension> {
            awsCoreDependency =
                libs.lib("springCloudAws").get()

            awsS3Dependency =
                libs.lib("springCloudAwsS3").get()
        }

        // ---------------------------------------------------------------------
        // Database
        // ---------------------------------------------------------------------
        project.extensions.configure<CommonDBDependenciesPlugin.DbExtension> {
            springJdbc =
                libs.lib("springJdbc").get()

            springDataJpa =
                libs.lib("springDataJpa").get()

            mysqlConnector =
                libs.lib("mysql").get()

            redis =
                libs.lib("springDataRedis").get()

            jedis =
                libs.lib("jedis").get()

            jooq =
                libs.lib("jooq").get()
        }

        // ---------------------------------------------------------------------
        // Session
        // ---------------------------------------------------------------------
        project.extensions.configure<CommonSessionDependencies.SessionExtension> {

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
        project.extensions.configure<RumpusTest.TestExtension> {

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

        project.extensions.configure<RumpusTestConventions.TestConventionsExtension> {

            junitVersion =
                libs.lib("junit4").get()

            showStandardStreams = true
        }

        // ---------------------------------------------------------------------
        // OpenAPI
        // ---------------------------------------------------------------------
        project.extensions.configure<OpenApiDependenciesPlugin.OpenApiExtension> {

            springdocCore =
                libs.lib("openApiCore").get()
        }

        // ---------------------------------------------------------------------
        // Aggregated dependencies
        // ---------------------------------------------------------------------
        project.extensions.configure<RumpusDependenciesPlugin.RumpusDepsExtension> {

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
        // Coordinates
        // ---------------------------------------------------------------------
        project.group =
            libs.findVersion("commonGroup").get().requiredVersion

        project.version =
            libs.findVersion("common").get().requiredVersion

        // ---------------------------------------------------------------------
        // Dependencies
        // ---------------------------------------------------------------------
        project.dependencies.add(
            "implementation",
            libs.lib("junit").get()
        )

        // ---------------------------------------------------------------------
        // Jar configuration
        // ---------------------------------------------------------------------
        project.tasks.named<BootJar>("bootJar") {
            enabled = false
        }

        project.tasks.named<Jar>("jar") {
            enabled = true
        }
    }
}