package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.database.EntryModel
import dev.slne.surf.servertour.database.MemberModel
import dev.slne.surf.servertour.database.PoiModel
import dev.slne.surf.servertour.database.tables.EntryTable
import dev.slne.surf.servertour.database.tables.MemberTable
import dev.slne.surf.servertour.database.tables.PoiTable
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZonedDateTime
import java.util.*

object EntryManager {

    private val _entries: ObjectList<TourEntry> = mutableObjectListOf()
    val entries get() = _entries.freeze()

    fun generateUuid(): UUID {
        var uuid: UUID

        do {
            uuid = UUID.randomUUID()
        } while (
            _entries.any { it.uuid == uuid } ||
            _entries.flatMap { it.poi }.any { it.uuid == uuid }
        )

        return uuid
    }


    fun clearCache() = _entries.clear()

    fun listEntries(owner: UUID) = entries.filter { it.owner.uuid == owner }

    suspend fun fetch() = newSuspendedTransaction {
        val dbEntries = EntryModel.all()

        _entries.clear()
        _entries.addAll(dbEntries.map { TourEntry.fromModel(it) })
    }

    suspend fun create(entry: TourEntry) {
        createEntry(entry)
        _entries.add(entry)
    }

    private suspend fun createEntry(entry: TourEntry) = newSuspendedTransaction {
        val dbEntry = EntryModel.new {
            this.uuid = entry.uuid
            this.name = entry.name
            this.description = entry.description
            this.icon = entry.icon
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
    ) = PoiModel.new {
        this.entry = dbEntry
        this.location = poi.location
        this.uuid = poi.uuid
        this.name = poi.name
        this.description = poi.description
        this.owner = poi.owner.uuid
        this.icon = poi.icon
        this.createdAt = poi.createdAt
        this.updatedAt = poi.updatedAt
    }

    private fun createEntryMember(
        dbEntry: EntryModel,
        member: EntryMember
    ) = MemberModel.new {
        this.entry = dbEntry
        this.member = member.uuid
        this.description = member.description
        this.createdAt = member.createdAt
        this.updatedAt = member.updatedAt
    }

    suspend fun delete(entry: TourEntry) = newSuspendedTransaction {
        EntryTable.deleteWhere { EntryTable.uuid eq entry.uuid }
    }

    suspend fun deleteAll() = newSuspendedTransaction {
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

        newSuspendedTransaction {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction

            MemberModel.findSingleByAndUpdate((MemberTable.entry eq dbEntry.id) and (MemberTable.member eq member.uuid)) {
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


        newSuspendedTransaction {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }.firstOrNull()
                ?: return@newSuspendedTransaction

            PoiModel.findSingleByAndUpdate(
                (PoiTable.entry eq dbEntry.id) and (PoiTable.uuid eq poi.uuid)
            ) {
                it.name = poi.name
                it.owner = poi.owner.uuid
                it.description = poi.description
                it.icon = poi.icon
                it.location = poi.location
                it.updatedAt = poi.updatedAt
            }
        }
    }

    private suspend fun updateEntry(entry: TourEntry) = newSuspendedTransaction {
        EntryModel.findSingleByAndUpdate(EntryTable.uuid eq entry.uuid) {
            it.name = entry.name
            it.description = entry.description
            it.icon = entry.icon
            it.updatedAt = entry.updatedAt
            it.location = entry.location
        }
    }

    suspend fun runUpdating(entry: TourEntry, action: suspend (TourEntry) -> Unit) {
        entry.updatedAt = ZonedDateTime.now()
        action(entry)
    }

    suspend fun addMember(entry: TourEntry, member: Player, description: String? = null) =
        addMember(entry, member.uniqueId, description)

    suspend fun addMember(
        entry: TourEntry,
        member: UUID,
        description: String? = null
    ) = runUpdating(entry) {
        val entryMember = EntryMember(
            uuid = member,
            description = description ?: "",
        )

        val result = newSuspendedTransaction {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction false

            createEntryMember(dbEntry, entryMember)

            return@newSuspendedTransaction true
        }

        if (!result) return@runUpdating

        entry.addMember(member, description)
    }

    suspend fun removeMember(entry: TourEntry, member: Player) =
        removeMember(entry, member.uniqueId)

    suspend fun removeMember(entry: TourEntry, member: UUID) = runUpdating(entry) {
        val result = newSuspendedTransaction {
            val dbEntry = EntryModel.find { EntryTable.uuid eq entry.uuid }
                .firstOrNull() ?: return@newSuspendedTransaction false

            MemberTable.deleteWhere {
                (MemberTable.entry eq dbEntry.id) and
                        (MemberTable.member eq member)
            }

            return@newSuspendedTransaction true
        }

        if (!result) return@runUpdating

        entry.poi.filter { it.owner.uuid == member }.forEach { poi ->
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
        val result = newSuspendedTransaction {
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
        val result = newSuspendedTransaction {
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