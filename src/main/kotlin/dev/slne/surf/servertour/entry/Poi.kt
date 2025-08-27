package dev.slne.surf.servertour.entry

import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Location
import java.time.ZonedDateTime
import java.util.*

data class Poi(
    val entry: TourEntry,
    val uuid: UUID,
    var owner: EntryMember?,
    var name: String,
    var description: String,
    var status: EntryStatus,
    var location: Location,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) : ComponentLike {

    suspend fun submit() {
        if (!isDraft()) return

        EntryManager.updatePoi(entry, this) {
            it.status = EntryStatus.PENDING
        }

        status = EntryStatus.PENDING
    }

    suspend fun accept() {
        if (!isPending()) return

        EntryManager.updatePoi(entry, this) {
            it.status = EntryStatus.ACCEPTED
        }

        status = EntryStatus.ACCEPTED
    }

    suspend fun reject() {
        if (!isPending()) return

        EntryManager.updatePoi(entry, this) {
            it.status = EntryStatus.REJECTED
        }

        status = EntryStatus.REJECTED
    }

    suspend fun reopen() {
        if (!isLocked()) return

        EntryManager.updatePoi(entry, this) {
            it.status = EntryStatus.DRAFT
        }

        status = EntryStatus.DRAFT
    }

    fun isLocked() = status == EntryStatus.ACCEPTED || status == EntryStatus.REJECTED
    fun isDraft() = status == EntryStatus.DRAFT
    fun isPending() = status == EntryStatus.PENDING

    override fun asComponent() = buildText {
        variableValue(name.ifBlank { "Unbenannt" })

        hoverEvent(buildText {
            variableKey("Beschreibung: ")
            appendNewline()
            variableValue(description.ifBlank { "Keine Beschreibung vorhanden" })
            appendNewline(2)

            variableKey("Ersteller: ")
            appendNewline()
            variableValue(owner?.offlinePlayer?.name ?: "Unbekannt")
        })
    }
}