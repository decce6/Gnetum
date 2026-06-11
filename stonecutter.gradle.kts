plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    constants["sodium"] = node.project.hasProperty("deps.sodium")
    constants["sodium_legacy"] = node.project.hasProperty("deps.sodium_legacy")
    constants["jade"] = node.project.hasProperty("deps.jade")
    constants["xaerominimap"] = node.project.hasProperty("deps.xaerominimap")
    swaps["mod_version_short"] = "\"" + property("mod_version") + "\";"
    swaps["import_blend_factors"] = when {
        eval(current.version, ">=26.2") -> "import com.mojang.blaze3d.platform.BlendFactor;"
        else -> "import com.mojang.blaze3d.platform.DestFactor; import com.mojang.blaze3d.platform.SourceFactor;"
    }
    swaps["src_factor"] = when {
        eval(current.version, ">=26.2") -> "BlendFactor.$1"
        else -> "SourceFactor.$1"
    }
    swaps["dest_factor"] = when {
        eval(current.version, ">=26.2") -> "BlendFactor.$1"
        else -> "DestFactor.$1"
    }
    replacements.string(current.parsed >= "26.2") {
        replace("sourceAlpha()", "alpha().sourceFactor()")
        replace("destAlpha()", "alpha().destFactor()")
        replace("sourceColor()", "color().sourceFactor()")
        replace("destColor()", "color().destFactor()")
    }
    replacements.string(current.parsed >= "1.21.11") {
        replace("ResourceLocation", "Identifier")
        replace("location()", "identifier()")
        replace("com.mojang.blaze3d.platform.GlStateManager", "com.mojang.blaze3d.opengl.GlStateManager")
    }
    replacements.string(current.parsed >= "26.1") {
        replace("GuiGraphics", "GuiGraphicsExtractor")
        replace("net.minecraft.client.gui.render.state", "net.minecraft.client.renderer.state.gui")
        replace("submitText", "addText")
        replace("submitPicturesInPictureState", "addPicturesInPictureState")
        replace("submitItem", "addItem")
        replace("submitGuiElement", "addGuiElement")
    }
}

tasks.register("publishAll") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishMods"))
}

tasks.register("publishAllModrinth") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishModrinth"))
}

tasks.register("publishAllCurseForge") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishCurseforge"))
}

stonecutter.tasks {
    order("publishMods")
    order("publishModrinth")
    order("publishCurseforge")
}

