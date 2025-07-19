package dev.slne.surf.servertour.entry

import dev.slne.surf.servertour.entry.EntryManager.generateUuid
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Location
import org.jetbrains.annotations.Range
import java.util.*

object FakeDataManager {

    private fun uuid(uuidString: String) = UUID.fromString(uuidString)
    private val icons = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).toObjectList()

    private val possibleMembers = mutableObjectListOf(
        uuid("5c63e51b-82b1-4222-af0f-66a4c31e36ad"), // Ammo
        uuid("3a1d37a0-0a1d-419f-ac7a-f9201206cfd0"), // Keviro
        uuid("176b0e81-850b-4636-8c88-298284650823"), // Twisti
        uuid("1c779cb1-3860-4e23-9cac-7f160b2acc61"), // Red
        uuid("7632f80f-6137-4410-9438-40e4d3214d47"), // Jori
        uuid("42be33ea-fcaa-46c8-a390-319d869aecfd"), // Mikey
        uuid("796f04f9-db8b-4d18-8374-a4e3b9f48abe"), // Alex
        uuid("8326e706-cd48-4273-b1d3-9836aa44d666"), // Dorlino
        uuid("d0a55f1b-762a-49a4-b4be-cbc59cec0239"), // Timonso
        uuid("182cf41d-3324-4924-b68e-b98fc3ee5195"), // Tonne
        uuid("75c17e10-bd79-4824-9d13-90b09763012e"), // Jofield
    )

    suspend fun fakeData(
        owner: UUID,
        amount: Int = 10,
        poiAmount: IntRange = 1..10,
        memberAmount: @Range(from = 1, to = 10) IntRange = 1..10
    ) {
        val world = server.worlds.first()
        val ownerMember = EntryMember(
            uuid = owner,
            description = "This is the owner of the tour entry.",
        )

        for (i in 1..amount) {
            val entryX = (0..100).random().toDouble()
            val entryY = (0..100).random().toDouble()
            val entryZ = (0..100).random().toDouble()

            val entry = TourEntry(
                uuid = generateUuid(),
                name = "Tour Entry $i",
                description = "This is a fake tour entry for testing purposes. It is not real and should not be used in production.",
                icon = icons.random(),
                owner = ownerMember,
                location = Location(world, entryX, entryY, entryZ),
            )
            entry.addMember(owner, ownerMember.description)

            val remainingMembers = possibleMembers.filterNot { it == owner }.toMutableList()

            for (j in 1..memberAmount.random()) {
                val member = remainingMembers.randomOrNull() ?: continue
                remainingMembers.remove(member)
                entry.addMember(member)
            }

            for (j in 1..poiAmount.random()) {
                val poiX = (0..100).random().toDouble() + entryX
                val poiY = (0..100).random().toDouble() + entryY
                val poiZ = (0..100).random().toDouble() + entryZ

                val poi = Poi(
                    uuid = generateUuid(),
                    icon = icons.random(),
                    name = "POI $j",
                    description = "This is a fake tour poi for testing purposes.",
                    location = Location(world, poiX, poiY, poiZ),
                    owner = entry.members.random()
                )

                entry.addPoi(poi)
            }

            EntryManager.create(entry)
        }
    }
}