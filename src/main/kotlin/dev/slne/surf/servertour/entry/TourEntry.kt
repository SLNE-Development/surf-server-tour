package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.database.EntryModel
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemType
import java.time.ZonedDateTime
import java.util.*

data class TourEntry(
    val uuid: UUID,
    var icon: ItemType,
    var name: String,
    var description: String,
    val owner: UUID,
    var location: Location,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    private val _members: ObjectList<UUID> = mutableObjectListOf()
    private val _poi: ObjectList<Poi> = mutableObjectListOf()
    private val statusChanges: ObjectList<StatusChange> = mutableObjectListOf()

    val members get() = _members.freeze()
    val poi get() = _poi.freeze()

    fun addMember(member: UUID) = _members.add(uuid)
    fun addMember(player: Player) = addMember(player.uniqueId)

    fun removeMember(member: UUID) = _members.remove(member)
    fun removeMember(player: Player) = removeMember(player.uniqueId)

    fun addPoi(poi: Poi) = this._poi.add(poi)
    fun removePoi(poi: Poi) = this._poi.remove(poi)

    fun submit(player: Player) = submit(player.uniqueId)

    fun submit(submitter: UUID) {
        if (isLocked()) return

        statusChanges.add(StatusChange.pending(submitter))
    }

    fun accept(player: Player) = accept(player.uniqueId)

    fun accept(acceptedBy: UUID) {
        val active = getActiveStatus()

        if (active == null || active.newStatus != EntryStatus.PENDING) return

        statusChanges.add(StatusChange.accepted(acceptedBy))
    }

    @Suppress("SpellCheckingInspection")
    fun reject(rejector: Player, reason: String) = reject(rejector.uniqueId, reason)

    @Suppress("SpellCheckingInspection")
    fun reject(rejector: UUID, reason: String) {
        val active = getActiveStatus()

        if (active == null || active.newStatus != EntryStatus.PENDING) return

        statusChanges.add(StatusChange.rejected(rejector, reason))
    }

    fun reopen(player: Player) = reopen(player.uniqueId)

    fun reopen(reopenedBy: UUID) {
        val active = getActiveStatus()

        if (active != null && (active.newStatus == EntryStatus.REJECTED || active.newStatus == EntryStatus.ACCEPTED)) {
            return
        }

        statusChanges.add(StatusChange.reopen(reopenedBy, active!!.oldStatus))
    }

    fun isLocked(): Boolean {
        val active = getActiveStatus()

        return active != null && active.newStatus == EntryStatus.DRAFT
    }

    fun getActiveStatus() = statusChanges.lastOrNull()

    override fun toString(): String {
        return "ServerTourEntry(uuid=$uuid, icon=$icon, name='$name', description='$description', owner=$owner, location=$location, createdAt=$createdAt, updatedAt=$updatedAt, statusChanges=$statusChanges, members=$members, poi=$poi)"
    }

    companion object {
        fun fromModel(model: EntryModel) = TourEntry(
            uuid = model.uuid,
            icon = model.icon,
            name = model.name,
            description = model.description,
            owner = model.owner,
            location = model.location,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        ).also { entry ->
            entry._poi.addAll(model.pois.map { poiModel ->
                Poi.fromModel(poiModel)
            })
            entry._members.addAll(model.members.map { it.member })
            entry.statusChanges.addAll(model.statusChanges.map { change ->
                StatusChange.fromModel(change)
            }
            )
        }


    }
}

