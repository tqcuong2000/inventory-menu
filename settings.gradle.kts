pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikuGie Snapshots"
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    create(rootProject) {
        versions("1.21.11", "26.1-pre-1")
        vcsVersion = "1.21.11"
    }
}

rootProject.name = "inventory-menu"
