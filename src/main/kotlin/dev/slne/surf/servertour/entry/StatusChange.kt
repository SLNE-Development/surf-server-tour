package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.database.base.BaseStatusChangeModel
import java.time.ZonedDateTime
import java.util.*

data class StatusChange(
    val oldStatus: EntryStatus,
    val newStatus: EntryStatus,
    val changedBy: UUID,
    val changedReason: String? = null,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
) {
    companion object {
        fun fromModel(model: BaseStatusChangeModel) = StatusChange(
            oldStatus = model.oldStatus,
            newStatus = model.newStatus,
            changedBy = model.changedBy,
            changedReason = model.changedReason,
            createdAt = model.createdAt
        )

        fun pending(changedBy: UUID) = StatusChange(
            oldStatus = EntryStatus.DRAFT,
            newStatus = EntryStatus.PENDING,
            changedBy = changedBy,
            changedReason = null
        )

        fun accepted(changedBy: UUID) = StatusChange(
            oldStatus = EntryStatus.PENDING,
            newStatus = EntryStatus.ACCEPTED,
            changedBy = changedBy,
            changedReason = null
        )

        fun rejected(
            changedBy: UUID,
            changedReason: String
        ) = StatusChange(
            oldStatus = EntryStatus.PENDING,
            newStatus = EntryStatus.REJECTED,
            changedBy = changedBy,
            changedReason = changedReason
        )

        fun reopen(
            changedBy: UUID,
            oldStatus: EntryStatus
        ) = StatusChange(
            oldStatus = oldStatus,
            newStatus = EntryStatus.DRAFT,
            changedBy = changedBy,
            changedReason = null
        )
    }
}