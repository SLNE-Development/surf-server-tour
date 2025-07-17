package dev.slne.surf.servertour.database.tables

import dev.slne.surf.servertour.utils.zonedDateTime
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.jetbrains.exposed.dao.id.LongIdTable
import java.util.*

abstract class BaseTable(table: String) : LongIdTable(table) {

    val uuid = varchar("uuid", 36).transform(
        { UUID.fromString(it) },
        { it.toString() }
    ).uniqueIndex()
    val name = varchar("name", 64)
    val description = largeText("description")
    val icon = varchar("icon", 255).transform(
        { RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).getOrThrow(key(it)) },
        { it.key().asString() }
    )
    val owner = varchar("owner", 36).transform(
        { UUID.fromString(it) },
        { it.toString() }
    )

    val world = varchar("world", 36).transform(
        { UUID.fromString(it) },
        { it.toString() }
    )
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")

}