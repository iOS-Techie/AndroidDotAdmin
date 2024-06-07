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
                this.username = "mapbox"
                this.password = "sk.eyJ1Ijoic2FtaXJtYWthZGlhIiwiYSI6ImNsamR1cm9iMjJmOWkzY3F5MDA0anF6bW4ifQ.ct4TETDuxIdTR5YvBbXVNQ"
            }
        }
    }
}

rootProject.name = "DoT Admin"
include(":app")
include(":ucrop")
include(":imagepicker")
 