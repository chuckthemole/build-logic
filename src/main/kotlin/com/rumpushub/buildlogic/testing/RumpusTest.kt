package com.rumpushub.buildlogic.testing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * RumpusTest
 *
 * Provides standard test dependencies for Rumpus projects.
 * Versions can be configured via an extension or centralized version catalog.
 *
 * Example usage in build.gradle.kts:
 * ```
 * apply<RumpusTest>()
 *
 * testDeps {
 *     springBoot = rumpusLibs.springBootStarterTest
 *     mockito = rumpusLibs.mockito
 *     junitApi = rumpusLibs.junitApi
 *     junitEngine = rumpusLibs.junitEngine
 *     springSecurityTest = rumpusLibs.springSecurityTest
 * }
 * ```
 */
class RumpusTest : Plugin<Project> {

    open class TestExtension {
        var springBoot: Any? = null
        var mockito: Any? = null
        var junitApi: Any? = null
        var junitEngine: Any? = null
        var springSecurityTest: Any? = null
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("testDeps", TestExtension::class.java)

        project.afterEvaluate {
            val springBootDep = extension.springBoot ?: throw IllegalArgumentException("RumpusTest requires 'springBoot' to be set")
            val mockitoDep = extension.mockito ?: throw IllegalArgumentException("RumpusTest requires 'mockito' to be set")
            val junitApiDep = extension.junitApi ?: throw IllegalArgumentException("RumpusTest requires 'junitApi' to be set")
            val junitEngineDep = extension.junitEngine ?: throw IllegalArgumentException("RumpusTest requires 'junitEngine' to be set")
            val springSecurityDep = extension.springSecurityTest ?: throw IllegalArgumentException("RumpusTest requires 'springSecurityTest' to be set")

            project.dependencies {
                add("testImplementation", springBootDep)
                add("testImplementation", mockitoDep)
                add("testImplementation", junitApiDep)
                add("testRuntimeOnly", junitEngineDep)
                add("testImplementation", springSecurityDep)
            }

            project.logger.lifecycle("RumpusTest dependencies applied: $springBootDep, $mockitoDep, $junitApiDep, $junitEngineDep, $springSecurityDep")
        }
    }
}
