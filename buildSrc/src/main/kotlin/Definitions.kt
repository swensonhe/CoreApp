import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.addProjectRepos() {
    gradlePluginPortal()
    google()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://www.jitpack.io")
    maven("https://storage.googleapis.com/r8-releases/raw")
    maven("https://maven.google.com")
//    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://plugins.gradle.org/m2/")
    exclusiveContent {
        forRepositories(maven("https://raw.githubusercontent.com/swensonhe/strapi-kmm/maven"))
        filter { includeGroup("com.metacto") }
    }
}