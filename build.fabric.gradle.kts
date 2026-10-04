@file:OptIn(StonecutterExperimentalAPI::class)

import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.invoke
import kotlin.reflect.KProperty

// in stonecutter.gradle.kts
class CommonProperty<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T = (rootProject.extra[sc.current.project] as Map<String, Any?>)[property.name] as T
}

val modName: String by project
val modId: String by project
val modDescription: String by project
val modIcon: String by project
val license: String by project
val javaVersion by CommonProperty<JavaVersion>()
val finalFileName by CommonProperty<String>()

repositories {
	mavenCentral()
	gradlePluginPortal()
	google()
}

dependencies {
	compileOnly("com.google.code.gson:gson:2.14.0")
	compileOnly("org.slf4j:slf4j-api:2.0.17")
}

tasks {
	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}
		fun target(version: String) = ">=$version"
		val props = buildMap {
			register("modName", modName)
			register("modId", modId)
			register("modDescription", modDescription)
			register("modIcon", modIcon)
			register("version", version.toString())
			register("license", license)
		}
		filesMatching(listOf("fabric.mod.json")) { expand(props) }
        outputs.upToDateWhen { false }
	}

	register<Copy>("buildAndCollect") {
		group = "build"

		from(jar.map { it.archiveFile })
		into(rootProject.layout.buildDirectory.file("libs"))
		dependsOn("build")
	}
	jar {
		archiveFileName.set(finalFileName)
		manifest.attributes(
			mapOf(
				"Premain-Class" to "at.yedel.faux.launch.FauxAgent"
			)
		)
	}

}

java {
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
}