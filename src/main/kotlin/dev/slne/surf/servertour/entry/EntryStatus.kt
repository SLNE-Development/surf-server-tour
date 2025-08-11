package dev.slne.surf.servertour.entry

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.ComponentLike

enum class EntryStatus : ComponentLike {
    DRAFT,
    PENDING,
    ACCEPTED,
    REJECTED;

    override fun asComponent() = buildText {
        variableValue(name)
    }
}