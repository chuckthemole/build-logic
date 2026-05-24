package com.rumpushub.buildlogic.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * CommonDBDependenciesPlugin
 *
 * Adds database-related dependencies to a project.
 * Includes Spring Boot JDBC, JPA, MySQL, Redis, and jOOQ.
 *
 * Features:
 * - Centralized dependency management via version catalogs or explicit assignment.
 * - Type-safe Kotlin DSL configuration through a Gradle extension.
 * - Clear error messages if required versions/dependencies are not set.
 *
 * Example usage in build.gradle.kts:
 * ```
 * apply<CommonDBDependenciesPlugin>()
 *
 * dbDeps {
 *     springJdbc = rumpusLibs.springJdbc
 *     springDataJpa = rumpusLibs.springDataJpa
 *     mysqlConnector = rumpusLibs.mysql
 *     redis = rumpusLibs.redis
 *     jedis = rumpusLibs.jedis
 *     jooq = rumpusLibs.jooq
 * }
 * ```
 */
class CommonDBDependenciesPlugin : Plugin<Project> {

    /**
     * Extension exposed to the build script for configuring database dependencies.
     */
    open class DbExtension {
        var springJdbc: Any? = null
        var springDataJpa: Any? = null
        var mysqlConnector: Any? = null
        var redis: Any? = null
        var jedis: Any? = null
        var jooq: Any? = null
    }

    override fun apply(project: Project) {
        val extension = project.extensions.create("dbDeps", DbExtension::class.java)

        project.afterEvaluate {
            val jdbc = extension.springJdbc
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'springJdbc' to be set.")
            val jpa = extension.springDataJpa
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'springDataJpa' to be set.")
            val mysql = extension.mysqlConnector
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'mysqlConnector' to be set.")
            val redisDep = extension.redis
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'redis' to be set.")
            val jedisDep = extension.jedis
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'jedis' to be set.")
            val jooqDep = extension.jooq
                ?: throw IllegalArgumentException("CommonDBDependenciesPlugin requires 'jooq' to be set.")

            project.dependencies.add("implementation", jdbc)
            project.dependencies.add("implementation", jpa)
            project.dependencies.add("implementation", mysql)
            project.dependencies.add("implementation", redisDep)
            project.dependencies.add("implementation", jedisDep)
            project.dependencies.add("implementation", jooqDep)

            project.logger.lifecycle(
                "CommonDBDependenciesPlugin applied: $jdbc, $jpa, $mysql, $redisDep, $jedisDep, $jooqDep"
            )
        }
    }
}
