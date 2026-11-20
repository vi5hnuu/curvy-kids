pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CurvyKids"
include(":app")
// Voice packs (Play Asset Delivery): Hindi letters and the free Barakhadi letters arrive by
// themselves after install (fast-follow); each world's Pro voices only when asked (on-demand).
include(":voice_hi_letters", ":voice_hi_barakhadi")
include(":voice_pro_letters", ":voice_pro_numbers", ":voice_pro_nature", ":voice_pro_my_world", ":voice_pro_hindi")
 