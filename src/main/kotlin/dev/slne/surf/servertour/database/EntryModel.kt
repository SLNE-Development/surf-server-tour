package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.base.BaseModel
import dev.slne.surf.servertour.database.base.BaseStatusChangeModel
import dev.slne.surf.servertour.database.tables.EntryTable
import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.database.tables.PoiTable
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EntryModel(id: EntityID<Long>) : BaseModel(id, EntryTable) {

    companion object : LongEntityClass<EntryModel>(EntryTable)

    val pois by PoiModel referrersOn PoiTable.entry
    val members by MemberModel referrersOn MemberTable.entry
    val statusChanges by EntryStatusChangeModel referrersOn EntryTable.ServerTourEntryStatusChangeTable.entry

    class EntryStatusChangeModel(id: EntityID<Long>) : BaseStatusChangeModel(
        id, EntryTable.ServerTourEntryStatusChangeTable
    ) {
        companion object :
            LongEntityClass<BaseStatusChangeModel>(EntryTable.ServerTourEntryStatusChangeTable)

        var entry by EntryModel referencedOn EntryTable.ServerTourEntryStatusChangeTable.entry
    }

    override fun toString(): String {
        return "EntryModel(pois=$pois, members=$members, statusChanges=$statusChanges)"
    }

}