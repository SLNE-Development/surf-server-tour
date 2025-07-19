package dev.slne.surf.servertour.database

import dev.slne.surf.servertour.database.base.BaseModel
import dev.slne.surf.servertour.database.base.BaseStatusChangeModel
import dev.slne.surf.servertour.database.tables.PoiTable
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PoiModel(id: EntityID<Long>) : BaseModel(id, PoiTable) {

    companion object : LongEntityClass<PoiModel>(PoiTable)

    var entry by EntryModel referencedOn PoiTable.entry
    val statusChanges by PoiStatusChangeModel referrersOn PoiTable.PoiStatusChangeTable.poi

    class PoiStatusChangeModel(id: EntityID<Long>) : BaseStatusChangeModel(
        id, PoiTable.PoiStatusChangeTable
    ) {
        companion object : LongEntityClass<PoiStatusChangeModel>(PoiTable.PoiStatusChangeTable)

        var poi by PoiModel referencedOn PoiTable.PoiStatusChangeTable.poi
    }

    override fun toString(): String {
        return "PoiModel(entry=$entry, statusChanges=$statusChanges)"
    }

}