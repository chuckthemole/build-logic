// import spock.lang.Specification
// import org.gradle.testfixtures.ProjectBuilder
// import EnvLoader

// /**
//  * Specification test class for the {@link EnvLoader} utility.
//  * 
//  * This test verifies that the EnvLoader.loadDotEnv(Project) method correctly reads
//  * environment variables from a `.env` file located in the parent directory of the project root,
//  * and loads them into the Gradle project's extra properties (project.ext).
//  * 
//  * The `.env` file is expected to contain lines in the format KEY=VALUE,
//  * and comments (lines starting with '#') are ignored.
//  *
//  * Example .env contents:
//  * <pre>
//  *   GPR_USER=exampleuser
//  *   GPR_TOKEN=exampletoken123
//  * </pre>
//  */
// class EnvLoaderSpec extends Specification {

//     /**
//      * Test that EnvLoader.loadDotEnv correctly loads key-value pairs from a .env file
//      * into the Gradle project's extra properties.
//      */
//     def "loadDotEnv should load environment variables into project.ext"() {
//         given:
//         // Create a Gradle project using ProjectBuilder, rooted at the current working directory.
//         // This is important because EnvLoader looks in the *parent* directory of rootDir for the .env file.
//         def tempDir = File.createTempDir()
//         def envFile = new File(tempDir.parentFile, ".env")
//         envFile.text = """
//             GPR_USER=exampleuser
//             GPR_TOKEN=exampletoken123
//         """.stripIndent()
//         def project = ProjectBuilder.builder()
//             .withProjectDir(tempDir)
//             .build()

//         when:
//         // Load environment variables from the .env file using the EnvLoader
//         EnvLoader.loadDotEnv(project)

//         then:
//         // Debug output: Print all extra property keys
//         println "project.ext keys: " + project.ext.properties.keySet()

//         // Assertions to verify the .env variables were correctly loaded into project.ext
//         project.ext.get("GPR_USER") == "exampleuser"
//         project.ext.get("GPR_TOKEN") == "exampletoken123"
//     }
// }
