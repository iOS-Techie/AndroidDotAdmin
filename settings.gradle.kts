pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://jitpack.io"))
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(uri("https://jitpack.io"))
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoiZGlzaGFudDExMSIsImEiOiJjbTE0c29nNmEwMWpwMmpzYjBheTlrenlkIn0.uG8gf3QU3xVainwkxG3v9w"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "DoT Admin"
include(":app")
include(":ucrop")
include(":imagepicker")
 