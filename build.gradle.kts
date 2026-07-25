plugins {
    id ("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")
fun fullModVersion() = "${prop("mod_version")}+${prop("minecraft_version")}-forge"
val modid = prop("mod_id")
version = fullModVersion()
group = prop("mod_group_id")
base {
    archivesName = modid
}

repositories {
    maven {
        url = uri("https://maven.minecraftforge.net/")
    }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

loom {
    forge {
        mixinConfigs("gnetum.mixins.json")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${prop("minecraft_version")}-${prop("forge_version")}")

    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    implementation("io.github.llamalad7:mixinextras-common:0.5.4")
    include("io.github.llamalad7:mixinextras-forge:0.5.4")
    implementation("io.github.llamalad7:mixinextras-forge:0.5.4")

    implementation("maven.modrinth:immediatelyfast:1.5.0+1.20.4-forge")
    implementation("maven.modrinth:jade:11.13.2+forge")
    implementation("maven.modrinth:embeddium:0.3.31+mc1.20.1")
    implementation("maven.modrinth:gui-clock:1.20.1-4.7-fabric+forge+neo")
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
    }

    named<ProcessResources>("processResources") {
        val propMap = mutableMapOf<String, Any>().apply {
            project.properties.forEach { (k, v) -> put(k, v.toString()) }
            put("mod_version_full", fullModVersion())
        }
        inputs.property("propMap", propMap)
        filesMatching(listOf("**/mods.toml", "**/pack.mcmeta")) {
            expand(propMap)
        }
    }

    named<Jar>("jar") {
        manifest.attributes (
            "MixinConfigs" to "$modid.mixins.json"
        )
    }
}
