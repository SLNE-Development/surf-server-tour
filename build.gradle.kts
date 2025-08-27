import dev.slne.surf.surfapi.gradle.util.slnePublic
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

repositories {
    slnePublic()
}

dependencies {
    api("dev.slne.surf:surf-database:2.2.1-SNAPSHOT")
}

plugins.withType<JavaPlugin> {
    configure<JavaPluginExtension> {
        java.toolchain.languageVersion.set(JavaLanguageVersion.of(24))
    }
}

group = "dev.slne.surf"
version = findProperty("version") as String

surfPaperPluginApi {
    mainClass("dev.slne.surf.servertour.SurfServerTour")
    generateLibraryLoader(false)
    authors.add("Ammo")
    foliaSupported(true)

    runServer {
        withSurfApiBukkit()
    }
}