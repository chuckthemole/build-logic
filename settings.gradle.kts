rootProject.name = "build-logic"

dependencyResolutionManagement {
  versionCatalogs { create("rumpusLibs") { from(files("../gradle/rumpus.versions.toml")) } }
}
