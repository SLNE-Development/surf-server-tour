package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.database.MemberModel
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import java.time.ZonedDateTime
import java.util.*

data class EntryMember(
    val uuid: UUID,
    var description: String? = null,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    val offlinePlayer get() = server.getOfflinePlayer(uuid)

    companion object {
        fun fromModel(model: MemberModel) = EntryMember(
            uuid = model.member,
            description = model.description
        )
    }
}
