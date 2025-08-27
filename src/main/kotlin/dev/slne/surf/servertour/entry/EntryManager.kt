package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.config.config
import dev.slne.surf.servertour.database.EntryModel
import dev.slne.surf.servertour.database.MemberModel
import dev.slne.surf.servertour.database.PoiModel
import dev.slne.surf.servertour.database.tables.EntryTable
import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.database.tables.PoiTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZonedDateTime
import java.util.*

object EntryManager {

    suspend fun listEntries(owner: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        EntryModel.find { (EntryTable.owner eq owner) and (EntryTable.server eq config.serverName) }
            .map { it.toApi() }
    }

    suspend fun createEntry(entry: TourEntry) = newSuspendedTransaction(Dispatchers.IO) {
        val dbEntry = EntryModel.new {
            this.server = entry.server
            this.uuid = entry.uuid
            this.name = entry.name
            this.description = entry.description
            this.owner = entry.owner.uuid
            this.createdAt = entry.createdAt
            this.updatedAt = entry.updatedAt
            this.location = entry.location
        }

        entry.poi.forEach { poi ->
            createEntryPoi(dbEntry, poi)
        }

        entry.members.forEach { member ->
            createEntryMember(dbEntry, member)
        }
    }

    private fun createEntryPoi(
        dbEntry: EntryModel,
        poi: Poi
    ): PoiModel {
        val dbOwner = poi.owner?.let {
            MemberModel.find { MemberTable.uuid eq it.uuid }.firstOrNull()
        }

        return PoiModel.new {
            this.entry = dbEntry
            this.location = poi.location
            this.uuid = poi.uuid
            this.name = poi.name
            this.description = poi.description
            this.owner = dbOwner
            this.createdAt = poi.createdAt
            this.updatedAt = poi.updatedAt
        }
    }

    private fun createEntryMember(
        dbEntry: EntryModel,
        member: EntryMember
    ) = MemberModel.new {
        this.entry = dbEntry
        this.uuid = member.uuid
        this.description = member.description
        this.createdAt = member.createdAt
        this.updatedAt = member.updatedAt
    }

    suspend fun delete(entry: TourEntry) = newSuspendedTransaction(Dispatchers.IO) {
        EntryTable.deleteWhere { EntryTable.uuid eq entry.uuid }
    }

    suspend fun deleteAll() = newSuspendedTransaction(Dispatchers.IO) {
        EntryTable.deleteAll()
    }

    suspend fun updateEntry(
        entry: TourEntry,
        action: suspend (TourEntry) -> Unit
    ) = runUpdating(entry) {
        action(entry)
        updateEntry(entry)
    }


    suspend fun updateMember(
        entry: TourEntry,
        member: EntryMember,
        action: suspend (EntryMember) -> Unit
    ) = runUpdating(entry) {
        action(member)

        newSuspendedTransaction(Dispatchers.IO) {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction

            MemberModel.findSingleByAndUpdate((MemberTable.entry eq dbEntry.id) and (MemberTable.uuid eq member.uuid)) {
                it.description = member.description
                it.updatedAt = ZonedDateTime.now()
            }
        }
    }

    suspend fun updatePoi(
        entry: TourEntry,
        poi: Poi,
        action: suspend (Poi) -> Unit
    ) = runUpdating(entry) {
        action(poi)

        newSuspendedTransaction(Dispatchers.IO) {
            val dbMember = poi.owner?.let {
                MemberModel.find { MemberTable.uuid eq it.uuid }.firstOrNull()
            }

            PoiModel.findSingleByAndUpdate(
                (PoiTable.uuid eq poi.uuid)
            ) {
                it.name = poi.name
                it.owner = dbMember
                it.description = poi.description
                it.location = poi.location
                it.updatedAt = poi.updatedAt
                it.status = poi.status
            }
        }
    }

    private suspend fun updateEntry(
        entry: TourEntry
    ) = newSuspendedTransaction(Dispatchers.IO) {
        EntryModel.findSingleByAndUpdate(EntryTable.uuid eq entry.uuid) {
            it.name = entry.name
            it.description = entry.description
            it.updatedAt = entry.updatedAt
            it.location = entry.location
            it.status = entry.status
        }
    }

    suspend inline fun <T> runUpdating(entry: TourEntry, action: suspend (TourEntry) -> T?): T? {
        entry.updatedAt = ZonedDateTime.now()
        return action(entry)
    }

    suspend fun addMember(
        entry: TourEntry,
        member: UUID,
        description: String? = null
    ): EntryMember? = runUpdating(entry) {
        val entryMember = EntryMember(
            uuid = member,
            description = description ?: "",
        )

        val result = newSuspendedTransaction(Dispatchers.IO) {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction null

            createEntryMember(dbEntry, entryMember)

            return@newSuspendedTransaction entryMember
        } ?: return@runUpdating null

        entry.addMember(entryMember)

        return@runUpdating result
    }

    suspend fun removeMember(entry: TourEntry, member: UUID) = runUpdating(entry) {
        val result = newSuspendedTransaction(Dispatchers.IO) {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction false

            MemberTable.deleteWhere {
                (MemberTable.entry eq dbEntry.id) and (MemberTable.uuid eq member)
            }

            return@newSuspendedTransaction true
        }

        if (!result) return@runUpdating

        entry.poi.filter { it.owner?.uuid == member }.forEach { poi ->
            updatePoi(entry, poi) {
                it.owner = entry.owner
            }
        }

        entry.removeMember(member)
    }

    suspend fun addPoi(
        entry: TourEntry,
        poi: Poi
    ) = runUpdating(entry) {
        val result = newSuspendedTransaction(Dispatchers.IO) {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction false

            createEntryPoi(dbEntry, poi)

            return@newSuspendedTransaction true
        }

        if (!result) return@runUpdating

        entry.addPoi(poi)
    }

    suspend fun removePoi(
        entry: TourEntry,
        point: Poi
    ) = runUpdating(entry) {
        val result = newSuspendedTransaction(Dispatchers.IO) {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction false

            PoiModel.find {
                (PoiTable.entry eq dbEntry.id) and
                        (PoiTable.uuid eq point.uuid)
            }.forEach { it.delete() }

            return@newSuspendedTransaction true
        }

        if (!result) return@runUpdating

        entry.removePoi(point)
    }

}