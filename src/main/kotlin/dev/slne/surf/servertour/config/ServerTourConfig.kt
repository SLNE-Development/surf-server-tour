package dev.slne.surf.servertour.config

import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

val config by lazy {
    surfConfigApi.createSpongeYmlConfig<ServerTourConfig>(plugin.dataPath, "config.yml")
}

@ConfigSerializable
data class ServerTourConfig(
    val serverName: String
)