import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.plugin.devel.PluginDeclaration

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
  gradlePluginPortal() // Required for Gradle plugins
}

// Apply Formatting
// apply<com.rumpushub.buildlogic.plugins.SpotlessPlugin>()

// --------------------------------------------------------------------------
// Dependencies
// --------------------------------------------------------------------------
// Core dependencies needed for plugin compilation and testing.
val libs = extensions.getByType<VersionCatalogsExtension>().named("rumpusLibs")

fun lib(alias: String) =
    libs.findLibrary(alias).orElseThrow {
      IllegalArgumentException("Missing library alias: $alias")
    }

val spotlessVersion = libs.findVersion("spotless").get().requiredVersion

val spotlessPlugin = "com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion"

dependencies {
  // Kotlin DSL support
  implementation(kotlin("gradle-plugin"))

  // Spotless plugin dependency
  implementation(spotlessPlugin)

  // Catalog dependencies
  implementation(lib("springBootWeb").get())
  implementation(lib("junit").get())

  // Spring Boot Gradle plugin
  implementation(
      "org.springframework.boot:spring-boot-gradle-plugin:${
            libs.findVersion("springBoot").get().requiredVersion
        }")

  // Dependency management plugin
  implementation(
      "io.spring.gradle:dependency-management-plugin:${
            libs.findVersion("dependencyManagement").get().requiredVersion
        }")

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
        implementationClass = "com.rumpushub.buildlogic.formatting.SpotlessPlugin")

    // -----------------------------------------------------------------
    // Publishing
    // -----------------------------------------------------------------
    registerConventionPlugin(
        name = "publishingConventions",
        id = "com.rumpushub.publishing",
        implementationClass = "com.rumpushub.buildlogic.publishing.CommonPublisherPlugin")

    // -----------------------------------------------------------------
    // Services
    // -----------------------------------------------------------------
    registerConventionPlugin(
        name = "adminService",
        id = "com.rumpushub.services.admin-service",
        implementationClass = "com.rumpushub.buildlogic.services.AdminServicePlugin")

    registerConventionPlugin(
        name = "commonLibrary",
        id = "com.rumpushub.services.common-library",
        implementationClass = "com.rumpushub.buildlogic.services.CommonLibraryPlugin")

    registerConventionPlugin(
        name = "rumpusApplication",
        id = "com.rumpushub.services.rumpus-application",
        implementationClass = "com.rumpushub.buildlogic.services.RumpusApplicationPlugin")
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
