package dev.slne.surf.servertour.database.base

import dev.slne.surf.servertour.database.tables.BaseStatusChangeTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID

abstract class BaseStatusChangeModel(
    id: EntityID<Long>, table: BaseStatusChangeTable
) : LongEntity(id) {
    var oldStatus by table.oldStatus
    var newStatus by table.newStatus
    var changedBy by table.changedBy
    var changedReason by table.changedReason
    var createdAt by table.createdAt
}