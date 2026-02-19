import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("me.decce.gnetum.gradle.gnetum-common-conventions")
    id("net.fabricmc.fabric-loom") version "1.14-SNAPSHOT"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")


dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:0.18.4")
    implementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")

    if (hasProperty("deps.sodium")) {
        compileOnly("${prop("deps.sodium")}")
    }
    if (hasProperty("deps.jade")) {
        compileOnly("maven.modrinth:jade:${prop("deps.jade")}")
    }
}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier = ""
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(shadowJar)
        from(shadowJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = tasks.shadowJar.get().archiveFile
}