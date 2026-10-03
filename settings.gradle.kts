pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net")
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.10-alpha.10"
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
	create(rootProject) {
		version("fabric", "26.3").buildscript("build.fabric.gradle.kts")
	}
}