import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.artifacts.VersionCatalogsExtension

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration
import org.gradle.api.NamedDomainObjectContainer

// --------------------------------------------------------------------------
// buildSrc/build.gradle.kts
// --------------------------------------------------------------------------
// This build file configures the buildSrc project, which contains custom
// Gradle plugins and build logic for the main project. Using Kotlin DSL
// provides type-safety, IDE autocompletion, and better maintainability.
// --------------------------------------------------------------------------

plugins {
  // Groovy support for legacy plugin scripts and Spock testing
  // groovy

  // Gradle plugin development support
  // `groovy-gradle-plugin`

  // Kotlin JVM plugin for writing build logic in Kotlin
  // kotlin("jvm") version "1.9.25"
  `kotlin-dsl`
}

// --------------------------------------------------------------------------
// Repositories
// --------------------------------------------------------------------------
// Where to resolve plugin and library dependencies from.
repositories {
  mavenCentral() // Standard Maven repository
  gradlePluginPortal()  // Required for Gradle plugins
}

// Apply Formatting
// apply<com.rumpushub.buildlogic.plugins.SpotlessPlugin>()

// --------------------------------------------------------------------------
// Dependencies
// --------------------------------------------------------------------------
// Core dependencies needed for plugin compilation and testing.
val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("rumpusLibs")
val spotlessVersion = libs
    .findVersion("spotless")
    .get().requiredVersion
val spotlessPlugin = "com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion"

dependencies {
  implementation(kotlin("gradle-plugin"))
  implementation(spotlessPlugin)

  // Gradle API allows writing custom plugins
  // implementation(gradleApi())

  // Local Groovy runtime for Groovy scripts
  // implementation(localGroovy())

  // Kotlin standard library for JVM
  // implementation(kotlin("stdlib-jdk8"))

  // Testing framework for Groovy (Spock 2.x supports JUnit Platform)
  // testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
  // Uncomment if needed for full Groovy runtime tests
  // testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
}

fun NamedDomainObjectContainer<PluginDeclaration>.registerConventionPlugin(
    name: String,
    id: String,
    implementationClass: String
) {
    register(name) {
        this.id = id
        this.implementationClass = implementationClass
    }
}

gradlePlugin {

    plugins {

        // -----------------------------------------------------------------
        // Formatting
        // -----------------------------------------------------------------
        registerConventionPlugin(
            name = "spotlessConvention",
            id = "com.rumpushub.spotless",
            implementationClass =
                "com.rumpushub.buildlogic.formatting.SpotlessPlugin"
        )

        // -----------------------------------------------------------------
        // Dependency Bundles
        // -----------------------------------------------------------------
        registerConventionPlugin(
            name = "sessionDependencies",
            id = "com.rumpushub.session-dependencies",
            implementationClass =
                "com.rumpushub.buildlogic.dependencies.CommonSessionDependencies"
        )

        registerConventionPlugin(
            name = "awsDependencies",
            id = "com.rumpushub.aws-dependencies",
            implementationClass =
                "com.rumpushub.buildlogic.dependencies.AwsDependenciesPlugin"
        )

        registerConventionPlugin(
            name = "dbDependencies",
            id = "com.rumpushub.db-dependencies",
            implementationClass =
                "com.rumpushub.buildlogic.dependencies.CommonDBDependenciesPlugin"
        )

        // -----------------------------------------------------------------
        // Conventions
        // -----------------------------------------------------------------
        registerConventionPlugin(
            name = "javaConventions",
            id = "com.rumpushub.java-conventions",
            implementationClass =
                "com.rumpushub.buildlogic.conventions.RumpusJavaConventionsPlugin"
        )

        registerConventionPlugin(
            name = "testingConventions",
            id = "com.rumpushub.testing-conventions",
            implementationClass =
                "com.rumpushub.buildlogic.testing.RumpusTestConventions"
        )

        // -----------------------------------------------------------------
        // Publishing
        // -----------------------------------------------------------------
        registerConventionPlugin(
            name = "publishingConventions",
            id = "com.rumpushub.publishing",
            implementationClass =
                "com.rumpushub.buildlogic.publishing.CommonPublisherPlugin"
        )
        
        registerConventionPlugin(
            name = "adminService",
            id = "com.rumpushub.services.admin-service",
            implementationClass =
                "com.rumpushub.buildlogic.services.AdminServicePlugin"
        )
    }
}

// --------------------------------------------------------------------------
// Test Configuration
// --------------------------------------------------------------------------
// Configures test logging and framework.
// tasks.test {
//     // Use JUnit Platform to support Spock 2.x
//     useJUnitPlatform()

//     // Ignore old Groovy test files
//     // Example: all files ending with *GroovyTest.groovy
//     exclude("**/*.groovy")

//     // Log test results to the console
//     testLogging {
//         events("passed", "skipped", "failed")
//         exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//         showStandardStreams = true
//     }
// }

// --------------------------------------------------------------------------
// Notes
// --------------------------------------------------------------------------
// 1. Keep Kotlin and Groovy plugin versions in sync with project requirements.
// 2. Gradle API and localGroovy() are mandatory for custom plugin compilation.
// 3. All test dependencies are isolated to the buildSrc project.
// 4. Avoid adding project-specific dependencies here; this is strictly for
//    plugin/build logic.
// 5. Using Kotlin DSL improves type safety and allows IDE autocompletion.
// --------------------------------------------------------------------------
