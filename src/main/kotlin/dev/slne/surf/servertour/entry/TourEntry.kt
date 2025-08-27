package dev.slne.surf.servertour.entry

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Location
import java.time.ZonedDateTime
import java.util.*

data class TourEntry(
    val server: String,
    val uuid: UUID,
    var name: String,
    var description: String,
    var status: EntryStatus,
    val owner: EntryMember,
    var location: Location,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
) : ComponentLike {
    private val _members = mutableObjectSetOf<EntryMember>()
    private val _poi: ObjectList<Poi> = mutableObjectListOf()

    val members get() = _members.freeze()
    val poi get() = _poi.freeze()

    fun addMember(member: EntryMember) = _members.add(member)
    fun removeMember(member: EntryMember) = _members.remove(member)
    fun removeMember(uuid: UUID) = _members.removeIf { it.uuid == uuid }

    fun addPoi(poi: Poi) = this._poi.add(poi)
    fun removePoi(poi: Poi) = this._poi.remove(poi)

    fun addMembers(members: Collection<EntryMember>) =
        _members.addAll(members)

    fun addPois(pois: Collection<Poi>) =
        _poi.addAll(pois)

    suspend fun submit() {
        println("Submitting entry $this")
        if (!isDraft()) return
        println("Entry is draft, submitting POIs...")

        poi.forEach { it.submit() }

        println("All POIs submitted, submitting entry...")

        EntryManager.updateEntry(this) {
            it.status = EntryStatus.PENDING
        }

        println("Entry submitted.")

        status = EntryStatus.PENDING

        println("Entry status updated to PENDING.")
    }

    suspend fun accept() {
        if (!isPending()) return

        EntryManager.updateEntry(this) {
            it.status = EntryStatus.ACCEPTED
        }

        status = EntryStatus.ACCEPTED
    }

    suspend fun reject() {
        if (!isPending()) return

        EntryManager.updateEntry(this) {
            it.status = EntryStatus.REJECTED
        }

        status = EntryStatus.REJECTED
    }

    suspend fun reopen() {
        if (!isLocked()) return

        EntryManager.updateEntry(this) {
            it.status = EntryStatus.DRAFT
        }

        status = EntryStatus.DRAFT
    }

    fun isLocked() = status == EntryStatus.ACCEPTED || status == EntryStatus.REJECTED
    fun isDraft() = status == EntryStatus.DRAFT
    fun isPending() = status == EntryStatus.PENDING

    override fun asComponent() = buildText {
        variableValue(name)
    }
}

