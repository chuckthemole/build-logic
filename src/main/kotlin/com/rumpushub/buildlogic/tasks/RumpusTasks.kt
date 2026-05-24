package com.rumpushub.buildlogic.tasks

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Defines custom tasks for Rumpus projects.
 *
 * Currently registers:
 * - printClasspath: prints the runtime classpath
 */
class RumpusTasks : Plugin<Project> {
    override fun apply(project: Project) {
        // --------------------------
        // Register "printClasspath" task
        // --------------------------
        project.tasks.register("printClasspath", PrintClasspathTask::class.java)
    }
}

/**
 * Custom task to print the runtime classpath.
 */
open class PrintClasspathTask : DefaultTask() {
    init {
        group = "Rumpus"
        description = "Prints the runtime classpath"
    }

    @TaskAction
    fun printClasspath() {
        val classpath = project.configurations.getByName("runtimeClasspath").asPath
        println("Runtime classpath: $classpath")
    }
}
