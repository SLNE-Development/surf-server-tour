package dev.slne.surf.servertour.database.tables

import dev.slne.surf.servertour.entry.EntryStatus
import dev.slne.surf.servertour.utils.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import java.util.*

abstract class BaseStatusChangeTable(table: String) : LongIdTable(table) {
    val oldStatus =
        enumerationByName<EntryStatus>("old_status", 255)
    val newStatus =
        enumerationByName<EntryStatus>("new_status", 255)
    val changedBy = varchar("changed_by", 36).transform(
        { UUID.fromString(it) },
        { it.toString() }
    )
    val changedReason = largeText("changed_reason").nullable()
    val createdAt = zonedDateTime("created_at")
}