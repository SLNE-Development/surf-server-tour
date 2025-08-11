@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun onePoiDialog(entry: TourEntry, poi: Poi): Dialog = dialog {
    base {
        title { info(poi.name) }
        externalTitle {
            text(poi.name)
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("Beschreibung: ")
                appendNewline()

                variableValue(poi.description.ifBlank { "Keine Beschreibung vorhanden" })
            }
        }
    }

    type {
        multiAction {
            action(changeDescriptionButton(entry, poi))
            action(removePoiButton(entry, poi))

            exitAction {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu den POIs") }
                    playerCallback {
                        it.showDialog(ownTourPoisDialog(entry))
                    }
                }
            }
        }
    }
}

private fun changeDescriptionButton(entry: TourEntry, poi: Poi) = actionButton {
    label { text("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des POI") }

    action {
        playerCallback { player ->
            player.showDialog(changePoiDescriptionDialog(entry, poi))
        }
    }
}

private fun removePoiButton(entry: TourEntry, poi: Poi) = actionButton {
    label { error("Entfernen") }
    tooltip { error("Entfernt den POI von der Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(removePoiDialog(entry, poi))
        }
    }
}
