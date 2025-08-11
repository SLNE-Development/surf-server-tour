@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun ownTourPoisDialog(entry: TourEntry, editable: Boolean): Dialog = dialog {
    val dialogs = buildDialogList(entry, editable).sortedBy { it.first.name }

    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("POIs: ")
                variableValue(entry.poi.size)
            }
        }
    }

    type {
        if (dialogs.isEmpty()) {
            notice(backButton(entry, editable))
        } else {
            multiAction {
                columns(3)
                exitAction(backButton(entry, editable))

                dialogs.forEach {
                    action {
                        label { text(it.first.name) }
                        tooltip { info("Öffne POI: ${it.first.name}") }
                        width(200)

                        action {
                            playerCallback { player -> player.showDialog(it.second) }
                        }
                    }
                }
            }
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourDialog(entry, editable))
        }
    }
}

private fun buildDialogList(entry: TourEntry, editable: Boolean) =
    entry.poi.map { it to onePoiDialog(entry, it, editable) }

