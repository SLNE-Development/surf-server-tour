@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.servertour.dialogs.own.review.submitted

import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.registry.data.dialog.DialogBase

fun createSubmittedTourPoIDialog(entry: TourEntry, poi: Poi, showcase: Boolean = false) = dialog {
    base {
        title {
            primary(entry.name)
            spacer(" - ")
            variableValue(poi.name)
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("Besitzer: ")
                appendNewline()
                variableValue(poi.owner?.offlinePlayer?.name ?: "Nicht angegeben")
                appendNewline(2)
                variableKey("Beschreibung: ")
                appendNewline()
                variableValue(poi.description)
            }
        }
    }

    type {
        multiAction {
            action(teleportButton(poi))
            exitAction(backButton(entry, showcase))
        }
    }
}

private fun teleportButton(poi: Poi) = actionButton {
    label { info("Teleportieren") }
    tooltip { info("Teleportiere dich zu diesem PoI") }

    action {
        playerCallback { player ->
            player.teleportAsync(poi.location)
            player.clearDialogs()
        }
    }
}

private fun backButton(entry: TourEntry, showcase: Boolean) = actionButton {
    label { error("Zurück") }
    tooltip { info("Zurück zu den PoIs") }

    action {
        playerCallback { player ->
            player.showDialog(createSubmittedTourPoIsDialog(entry, showcase))
        }
    }
}
