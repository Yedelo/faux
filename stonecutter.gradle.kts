import kotlin.reflect.KProperty
import kotlin.text.replace

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

val modName: String by project
val modId: String by project
val modIcon: String by project
val modrinthLogoLink: String by project

stonecutter active "fabric"

stonecutter parameters {
    val loader = current.project
    constants {
        match(loader, "fabric", "neoforge")
    }
    swaps["version"] = "\"${version}\";"
    swaps["loader"] = "\"${loader}\";"
    val shared = mutableMapOf<String, Any?>()
    extra[current.project] = shared

    class Declare<T>(private val value: T) {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Declare<T> {
            shared[property.name] = value
            return this
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    }
    // scream in their faces
    val finalFileName by Declare("$modName-$version+$loader-AGENT.jar")
    val modrinthReadme by Declare(rootProject.file("README.md").readText()
        .replace("src/main/resources/$modIcon", modrinthLogoLink)
    )
}