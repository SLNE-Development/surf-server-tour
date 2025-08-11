package dev.slne.surf.servertour.database.tables

import dev.slne.surf.database.database.columns.CurrentZonedDateTime
import dev.slne.surf.database.database.columns.zonedDateTime
import dev.slne.surf.servertour.entry.EntryStatus
import org.jetbrains.exposed.dao.id.LongIdTable

abstract class BaseTable(table: String) : LongIdTable(table) {

    val uuid = uuid("uuid").uniqueIndex()
    val name = varchar("name", 64)
    val description = largeText("description")
    val status = enumerationByName<EntryStatus>("status", 32).default(EntryStatus.DRAFT)

    val world = uuid("world")
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")

    val createdAt = zonedDateTime("created_at").defaultExpression(CurrentZonedDateTime)
    val updatedAt = zonedDateTime("updated_at").defaultExpression(CurrentZonedDateTime)

}