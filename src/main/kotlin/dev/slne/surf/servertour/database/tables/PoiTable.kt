package dev.slne.surf.servertour.database.tables

import org.jetbrains.exposed.sql.ReferenceOption

object PoiTable : BaseTable("servertour_entry_pois") {

    val entry = reference("entry_id", EntryTable.id, ReferenceOption.CASCADE)

    object PoiStatusChangeTable :
        BaseStatusChangeTable("servertour_entry_pois_status_changes") {
        val poi = reference("poi_id", PoiTable.id, ReferenceOption.CASCADE)
    }

}