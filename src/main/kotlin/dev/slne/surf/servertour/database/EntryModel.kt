package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.base.BaseModel
import dev.slne.surf.servertour.database.tables.EntryTable
import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.database.tables.PoiTable
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EntryModel(id: EntityID<Long>) : BaseModel(id, EntryTable) {

    companion object : LongEntityClass<EntryModel>(EntryTable)

    var owner by EntryTable.owner
    val pois by PoiModel referrersOn PoiTable.entry
    val members by MemberModel referrersOn MemberTable.entry

    fun toApi() = TourEntry(
        uuid = uuid,
        name = name,
        description = description,
        status = status,
        owner = EntryMember(
            uuid = owner
        ),
        location = location,
        createdAt = createdAt,
        updatedAt = updatedAt
    ).apply {
        addMembers(this@EntryModel.members.map { it.toApi() })
        addPois(this@EntryModel.pois.map { it.toApi(this) })
    }

}