package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.database.PoiModel
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Location
import org.bukkit.inventory.ItemType
import java.time.ZonedDateTime
import java.util.*

data class Poi(
    val uuid: UUID,
    var owner: EntryMember,
    var icon: ItemType,
    var name: String,
    var description: String,
    val location: Location,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) : ComponentLike {
    private val _statusChanges = mutableObjectListOf<StatusChange>()
    val statusChanges get() = _statusChanges.freeze()

    companion object {
        fun fromModel(entry: TourEntry, model: PoiModel) = Poi(
            uuid = model.uuid,
            owner = entry.members.firstOrNull { it.uuid == model.owner }
                ?: error("No owner found for POI $model"),
            icon = model.icon,
            name = model.name,
            description = model.description,
            location = model.location,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        ).also {
            it._statusChanges.addAll(model.statusChanges.map { change ->
                StatusChange.fromModel(change)
            })
        }
    }

    override fun asComponent() = buildText {
        variableValue(name.ifBlank { "Unbenannt" })

        hoverEvent(buildText {
            variableKey("Beschreibung: ")
            appendNewline()
            variableValue(description.ifBlank { "Keine Beschreibung vorhanden" })
            appendNewline(2)

            variableKey("Ersteller: ")
            appendNewline()
            variableValue(owner.offlinePlayer.name ?: "Unbekannt")
        })
    }
}