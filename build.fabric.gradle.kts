@file:OptIn(StonecutterExperimentalAPI::class)

import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.invoke
import kotlin.reflect.KProperty
import me.modmuss50.mpp.ReleaseType

plugins {
	id("dev.kikugie.loom-back-compat")
	id("me.modmuss50.mod-publish-plugin")
}

repositories {
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://maven.isxander.dev/releases")
}

// in stonecutter.gradle.kts
class CommonProperty<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T = (rootProject.extra[sc.current.project] as Map<String, Any?>)[property.name] as T
}
val modName: String by project
val modId: String by project
val modDescription: String by project
val modIcon: String by project
val versionType: String by project
val license: String by project
val modrinthId: String by project
val yaclVersion by CommonProperty<String>()
val javaVersion by CommonProperty<JavaVersion>()
val rangedVersion by CommonProperty<Boolean>()
val maxMc by CommonProperty<String?>()
val finalFileName by CommonProperty<String>()
val modrinthReadme by CommonProperty<String>()

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	loomx.applyMojangMappings()
	modImplementation("net.fabricmc:fabric-loader:${property("versions.fabricLoader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("versions.fabricApi")}")

	modApi("com.terraformersmc:modmenu:${property("versions.modMenu")}")
	modImplementation("dev.isxander:yet-another-config-lib:$yaclVersion")
}

loom {
	accessWidenerPath = sc.process(
		rootProject.file("src/main/resources/$modId.classtweaker"),
		"build/processed.classtweaker"
	)
	runConfigs.all {
		runDir = "../../run"
	}
}

tasks {
	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}
		exclude("META-INF/neoforge.mods.toml")

		fun target(version: String) = ">=$version"
		val props = buildMap {
			register("modName", modName)
			register("modId", modId)
			register("modDescription", modDescription)
			register("modIcon", modIcon)
			register("version", version.toString())
			register("license", license)
			register("yacl", target(yaclVersion))
			register("java", target(javaVersion.majorVersion))
			register("fabricLoader", target(sc.properties["versions.fabricLoader"]))
			val minecraftDependency =
				if (rangedVersion) ">=${sc.current.version} <=${maxMc}" else sc.current.version
			register("minecraft", minecraftDependency)
			register("mixinJava", "JAVA_${javaVersion.majorVersion}")
		}
		filesMatching(listOf("fabric.mod.json", "$modId.mixins.json5")) { expand(props) }

        outputs.upToDateWhen { false }
	}

	register<Copy>("buildAndCollect") {
		group = "build"

		from(loomx.modJar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs"))
		dependsOn("build")
	}
	loomx.modJar {
		archiveFileName.set(finalFileName)
	}
}

publishMods {
	file.set(loomx.modJar.map { it.archiveFile.get() })
	changelog.set(rootProject.file("CHANGELOG.md").readText())
	type.set(ReleaseType.of(versionType))
	modLoaders.add("fabric")

	modrinth {
		displayName.set("${project.version.toString()} for Fabric ${sc.current.version}")
		accessToken = System.getenv("MODRINTH_TOKEN")
		projectId.set(modrinthId)
		environment = CLIENT_ONLY
		projectDescription = modrinthReadme
		if (rangedVersion) {
			minecraftVersionRange {
				start = sc.current.version
				end = maxMc
			}
		}
		else {
			minecraftVersions.add(sc.current.version)
		}

		requires("fabric-api")
		requires("yacl")
		optional("modmenu")
	}
}

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}