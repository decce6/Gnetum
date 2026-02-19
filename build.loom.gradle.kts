import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("me.decce.gnetum.gradle.gnetum-common-conventions")
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")

    if (hasProperty("deps.sodium")) {
        modCompileOnly("${prop("deps.sodium")}")
    }
    if (hasProperty("deps.jade")) {
        modCompileOnly("maven.modrinth:jade:${prop("deps.jade")}")
    }
}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    named<RemapJarTask>("remapJar") {
        dependsOn(shadowJar)
        inputFile = shadowJar.flatMap { it.archiveFile }
        archiveClassifier = ""
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(remapJar)
        from(remapJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
}