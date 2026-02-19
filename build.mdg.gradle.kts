plugins {
    id("me.decce.gnetum.gradle.gnetum-common-conventions")
    id("net.neoforged.moddev") version "2.0.140"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")

neoForge {
    version = prop("deps.neoforge")
}

dependencies {
    if (hasProperty("deps.sodium")) {
        compileOnly("${prop("deps.sodium")}")
    }
    if (hasProperty("deps.jade")) {
        compileOnly("maven.modrinth:jade:${prop("deps.jade")}")
    }
}

val jijShadowJar = tasks.register<Jar>("jijShadowJar") {
    from(zipTree(tasks.shadowJar.map { it.archiveFile }))
    dependsOn(tasks.shadowJar)

    from(tasks.jarJar)
    dependsOn(tasks.jarJar)
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    assemble.get().dependsOn(jijShadowJar)

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(jijShadowJar)
        from(jijShadowJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = jijShadowJar.get().archiveFile
}