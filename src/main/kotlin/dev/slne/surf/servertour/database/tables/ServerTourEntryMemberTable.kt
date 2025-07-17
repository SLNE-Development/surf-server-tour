package dev.slne.surf.servertour.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object ServerTourEntryMemberTable : LongIdTable("servertour_entry_members") {

    val entry = reference("entry_id", EntryTable.id, ReferenceOption.CASCADE)
    val member = varchar("member_uuid", 36).transform(
        { UUID.fromString(it) },
        { it.toString() }
    )

}