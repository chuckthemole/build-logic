package com.rumpushub.buildlogic.utils

import java.io.File
import org.gradle.api.Project

/**
 * EnvLoader is a utility object that loads environment variables from a `.env` file, preferring the
 * current project directory but falling back to the parent directory.
 *
 * ### Behavior:
 * - Looks for a `.env` file in `project.rootDir`
 * - If not found, checks the parent directory
 * - Parses lines of the form `KEY=VALUE`, ignoring comments and malformed lines
 * - Sets each pair in `project.extensions.extraProperties` so they're available across the build
 *
 * ### Usage: EnvLoader.loadDotEnv(project)
 */
object EnvLoader {

    fun loadDotEnv(project: Project) {
        println("EnvLoader: Starting to load .env file")
        val rootDir = project.rootDir
        val parentDir = rootDir.parentFile

        println("EnvLoader: project.rootDir = $rootDir")

        var envFile = File(rootDir, ".env")
        if (!envFile.exists()) {
            println("EnvLoader: .env not found in rootDir, checking parentDir: $parentDir")
            envFile = File(parentDir, ".env")
        }

        if (envFile.exists()) {
            println("EnvLoader: Found .env file at ${envFile.absolutePath}")
            envFile.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                    val (rawKey, rawValue) = trimmed.split("=", limit = 2)
                    val key = rawKey.trim()
                    val value = rawValue.trim().removeSurrounding("\"").removeSurrounding("'")
                    println("EnvLoader: Setting project.extensions.extraProperties: $key = $value")
                    project.extensions.extraProperties.set(key, value)
                } else {
                    println("EnvLoader: Skipping line: '$line'")
                }
            }
        } else {
            println("EnvLoader: No .env file found in $rootDir or $parentDir")
        }

        println("EnvLoader: Done loading .env")
        println(
                "EnvLoader: project.extensions.extraProperties keys after load: ${project.extensions.extraProperties.properties.keys}"
        )
    }
}
