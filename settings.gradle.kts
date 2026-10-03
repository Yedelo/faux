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
	id("dev.kikugie.loom-back-compat") version "0.4.2"
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

loomx.loomVersion = "1.16-SNAPSHOT"

stonecutter {
	create(rootProject) {
		fun registerProject(versionString: String, loader: String) {
			version("$versionString-$loader", versionString).buildscript("build.$loader.gradle.kts")
		}

		for (versionString in listOf("1.21.4", "1.21.9", "1.21.11", "26.1", "26.2", "26.3")) {
			registerProject(versionString, "fabric")
		}
		registerProject("26.1", "neoforge")
		vcsVersion = "26.3-fabric"
	}
}