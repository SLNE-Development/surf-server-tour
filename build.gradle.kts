import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

group = "dev.slne.surf"

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api("dev.slne.surf:surf-database:2.0.4-SNAPSHOT")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.servertour.SurfServerTour")
    generateLibraryLoader(false)
    authors.add("Ammo")

    runServer {
        withSurfApiBukkit()
    }
}