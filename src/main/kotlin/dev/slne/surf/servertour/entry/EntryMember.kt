package dev.slne.surf.servertour.entry

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.ComponentLike
import java.time.ZonedDateTime
import java.util.*

data class EntryMember(
    val uuid: UUID,
    var description: String? = null,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) : ComponentLike {

    val offlinePlayer get() = server.getOfflinePlayer(uuid)

    override fun asComponent() = buildText {
        variableValue(offlinePlayer.name ?: "Unbekannt")
    }
}
