import org.gradle.api.Project

/**
 * EnvLoader is a utility class that loads environment variables from a `.env` file,
 * preferring the current project directory but falling back to the parent directory.
 *
 * ### Behavior:
 * - Looks for a `.env` file in `project.rootDir`
 * - If not found, checks the parent directory
 * - Parses lines of the form `KEY=VALUE`, ignoring comments and malformed lines
 * - Sets each pair in `project.ext` so they're available across the build
 *
 * ### Usage:
 * EnvLoader.loadDotEnv(project)
 */
class EnvLoader {

    static void loadDotEnv(Project project) {
        println "EnvLoader: Starting to load .env file"
        def rootDir = project.rootDir
        def parentDir = rootDir.parentFile

        println "EnvLoader: project.rootDir = ${rootDir}"

        File envFile = new File(rootDir, ".env")
        if (!envFile.exists()) {
            println "EnvLoader: .env not found in rootDir, checking parentDir: ${parentDir}"
            envFile = new File(parentDir, ".env")
        }

        if (envFile.exists()) {
            println "EnvLoader: Found .env file at ${envFile.absolutePath}"
            envFile.eachLine { line ->
                def trimmed = line.trim()
                if (!trimmed.startsWith("#") && trimmed.contains("=")) {
                    def (key, value) = trimmed.split("=", 2)
                    key = key.trim()
                    // This strips leading/trailing quotes (both single and double), and trims whitespace
                    value = value.trim().replaceAll(/^['"]|['"]$/, '')
                    println "EnvLoader: Setting project.ext property: ${key} = ${value}"
                    project.ext.set(key, value)
                } else {
                    println "EnvLoader: Skipping line: '${line}'"
                }
            }
        } else {
            println "EnvLoader: No .env file found in ${rootDir} or ${parentDir}"
        }

        println "EnvLoader: Done loading .env"
        println "EnvLoader: project.ext keys after load: ${project.ext.properties.keySet()}"
    }
}
