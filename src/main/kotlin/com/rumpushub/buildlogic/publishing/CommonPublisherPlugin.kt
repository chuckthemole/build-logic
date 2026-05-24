package com.rumpushub.buildlogic.publishing

import com.rumpushub.buildlogic.utils.EnvLoader
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

/**
 * CommonPublisherPlugin
 *
 * Configures Maven publishing for the `common` module of the Rumpus project.
 *
 * Features:
 * - Creates a Maven publication named `gpr` for the Java component.
 * - Sets up POM metadata including name, description, URL, licenses, developers, and SCM info.
 * - Configures publishing to GitHub Packages with credentials read from project properties or environment variables.
 * - Supports passing the version to publish via `-PcommonVersion`.
 */
class CommonPublisherPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        // --- Load environment variables from .env ---
        EnvLoader.loadDotEnv(project)

        project.afterEvaluate {

            // --- Determine version to publish ---
            val versionFromProperty = project.findProperty("commonVersion")?.toString()
            val publicationVersion = versionFromProperty ?: run {
                val versionFile = project.rootProject.file("gradle/rumpus.versions.toml")
                val fileText = versionFile.readText()

                // Only look in the [versions] section
                val versionsSection = Regex("""(?s)\[versions\](.*?)\n\[.*?\]""").find(fileText)?.groupValues?.get(1)
                    ?: Regex("""(?s)\[versions\](.*)""").find(fileText)?.groupValues?.get(1)
                    ?: throw GradleException("Could not find [versions] section in rumpus.versions.toml")

                // Find the first 'common = "..."' in that section
                val regex = Regex("""^\s*common\s*=\s*"(.+)"""", RegexOption.MULTILINE)
                regex.find(versionsSection)?.groupValues?.get(1)
                    ?: throw GradleException("Could not determine common module version from property or TOML file")
            }

            // --- Configure Maven publishing ---
            extensions.configure<org.gradle.api.publish.PublishingExtension>("publishing") {

                publications {
                    create("gpr", MavenPublication::class) {

                        // Attach Java component
                        try {
                            from(components["java"])
                        } catch (e: Exception) {
                            println("DEBUG: Failed to attach java component for project ${project.name}")
                            e.printStackTrace()
                        }

                        // Set explicit coordinates using version
                        groupId = "com.rumpushub.common"
                        artifactId = "common"
                        version = publicationVersion

                        // --- POM metadata ---
                        pom {
                            name.set("Rumpus Common Library")
                            description.set("Shared Java utilities and core classes for the Rumpus platform.")
                            url.set("https://github.com/chuckthemole/common")

                            licenses {
                                license {
                                    name.set("The Apache License, Version 2.0")
                                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }

                            developers {
                                developer {
                                    id.set("chuckthemole")
                                    name.set("Charles Thomas")
                                    email.set("chuckthemole@gmail.com")
                                }
                            }

                            scm {
                                connection.set("scm:git:git://github.com/chuckthemole/common.git")
                                developerConnection.set("scm:git:ssh://github.com:chuckthemole/common.git")
                                url.set("https://github.com/chuckthemole/common")
                            }
                        }
                    }
                }

                // --- Configure repositories ---
                repositories {
                    maven {
                        name = "GitHubPackages"
                        url = uri("https://maven.pkg.github.com/chuckthemole/common")
                        credentials {
                            username = project.findProperty("GPR_USER")?.toString()
                                ?: System.getenv("GPR_USER")
                            password = project.findProperty("GPR_TOKEN")?.toString()
                                ?: System.getenv("GPR_TOKEN")
                        }
                    }
                }
            }

            println("DEBUG: Configured 'gpr' publication with version $publicationVersion")
        }
    }
}
