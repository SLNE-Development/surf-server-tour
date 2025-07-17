package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.tables.ServerTourEntryMemberTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MemberModel(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MemberModel>(ServerTourEntryMemberTable)

    var member by ServerTourEntryMemberTable.member
    var entry by EntryModel referencedOn ServerTourEntryMemberTable.entry
}