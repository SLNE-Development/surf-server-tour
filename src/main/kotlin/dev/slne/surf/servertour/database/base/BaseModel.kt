package dev.slne.surf.servertour.database.base

import dev.slne.surf.servertour.database.tables.BaseTable
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import org.bukkit.Location
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID

abstract class BaseModel(id: EntityID<Long>, table: BaseTable) : LongEntity(id) {

    var uuid by table.uuid
    var name by table.name
    var description by table.description
    var icon by table.icon
    var owner by table.owner

    var world by table.world
    var x by table.x
    var y by table.y
    var z by table.z
    var yaw by table.yaw
    var pitch by table.pitch

    var createdAt by table.createdAt
    var updatedAt by table.updatedAt

    var location: Location
        get() {
            val bukkitWorld =
                server.getWorld(world) ?: error("World '$world' not found for entry $this.")

            return Location(bukkitWorld, x, y, z, yaw, pitch)
        }
        set(value) {
            world = value.world.uid
            x = value.x
            y = value.y
            z = value.z
            yaw = value.yaw
            pitch = value.pitch
        }

}