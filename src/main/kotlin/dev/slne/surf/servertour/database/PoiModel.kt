package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.base.BaseModel
import dev.slne.surf.servertour.database.tables.PoiTable
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PoiModel(id: EntityID<Long>) : BaseModel(id, PoiTable) {

    companion object : LongEntityClass<PoiModel>(PoiTable)

    var owner by MemberModel optionalReferencedOn PoiTable.owner
    var entry by EntryModel referencedOn PoiTable.entry

    fun toApi(entry: TourEntry) = Poi(
        uuid = uuid,
        owner = entry.members.firstOrNull { it.uuid == owner?.uuid },
        name = name,
        description = description,
        status = status,
        location = location,
        createdAt = createdAt,
        updatedAt = updatedAt,
        entry = entry,
    )

}