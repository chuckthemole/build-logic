package com.rumpushub.buildlogic.utils

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration
import org.gradle.api.NamedDomainObjectContainer

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