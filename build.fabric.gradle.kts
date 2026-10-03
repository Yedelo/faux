@file:OptIn(StonecutterExperimentalAPI::class)

import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.invoke
import kotlin.reflect.KProperty

// in stonecutter.gradle.kts
class CommonProperty<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T = (rootProject.extra[sc.current.project] as Map<String, Any?>)[property.name] as T
}

val javaVersion by CommonProperty<JavaVersion>()
val finalFileName by CommonProperty<String>()

repositories {
	mavenCentral()
	gradlePluginPortal()
	google()
}

dependencies {
	implementation("com.google.code.gson:gson:2.14.0")
}

tasks {
	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}
		fun target(version: String) = ">=$version"
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