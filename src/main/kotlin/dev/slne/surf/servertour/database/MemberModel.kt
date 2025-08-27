package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.entry.EntryMember
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MemberModel(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MemberModel>(MemberTable)

    var uuid by MemberTable.uuid
    var entry by EntryModel referencedOn MemberTable.entry
    var description by MemberTable.description
    var createdAt by MemberTable.createdAt
    var updatedAt by MemberTable.updatedAt

    fun toApi() = EntryMember(
        uuid = uuid,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}