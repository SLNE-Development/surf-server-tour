package dev.slne.surf.servertour.database.tables

import dev.slne.surf.servertour.utils.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object MemberTable : LongIdTable("servertour_entry_members") {

    val entry = reference(
        "entry_id", EntryTable.id,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val member = uuid("member_uuid")
    val description = largeText("description").nullable()

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")

}