package dev.slne.surf.servertour.database.tables

import org.jetbrains.exposed.sql.ReferenceOption

object PoiTable : BaseTable("servertour_entry_pois") {

    val owner = reference(
        "owner_id",
        MemberTable,
        onUpdate = ReferenceOption.SET_NULL,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val entry = reference(
        "entry_id",
        EntryTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

}