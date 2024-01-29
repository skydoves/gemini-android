@file:Suppress("UnstableApiUsage")
pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
rootProject.name = "gemini-android"
include(":app")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":feature:chat")
include(":feature:channels")
include(":baseline-profile")