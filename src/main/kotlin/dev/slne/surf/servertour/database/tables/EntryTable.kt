package dev.slne.surf.servertour.database.tables

import org.jetbrains.exposed.sql.ReferenceOption

object EntryTable : BaseTable("servertour_entries") {

    object ServerTourEntryStatusChangeTable :
        BaseStatusChangeTable("servertour_entries_status_changes") {
        val entry = reference("entry_id", EntryTable.id, ReferenceOption.CASCADE)
    }

}