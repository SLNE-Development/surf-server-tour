@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase

fun changePoiDescriptionDialog(entry: TourEntry, poi: Poi): Dialog = dialog {
    base {
        title { info("Beschreibung ändern") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            input {
                text("description") {
                    label { text("Neue Beschreibung") }
                    initial(poi.description)
                    width(600)
                    maxLength(Int.MAX_VALUE)
                    multiline(Int.MAX_VALUE, 300)
                }
            }
        }
    }

    type {
        confirmation(
            acceptChangeDescriptionButton(entry, poi),
            cancelChangeDescriptionButton(entry, poi)
        )
    }
}

private fun acceptChangeDescriptionButton(entry: TourEntry, poi: Poi) = actionButton {
    label { success("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des POIs") }

    action {
        customClick { response, audience ->
            val newDescription = response.getText("description") ?: return@customClick

            plugin.launch {
                val oldDescription = poi.description

                EntryManager.updatePoi(entry, poi) {
                    it.description = newDescription
                }

                audience.showDialog(
                    changePoiDescriptionSuccessNotice(
                        entry,
                        poi,
                        oldDescription
                    )
                )
            }
        }
    }
}

private fun cancelChangeDescriptionButton(entry: TourEntry, poi: Poi) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum Mitglied") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi))
        }
    }
}

private fun changePoiDescriptionSuccessNotice(
    entry: TourEntry,
    poi: Poi,
    oldDescription: String
) = dialog {
    base {
        title { primary("Beschreibung geändert") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Beschreibung des POI wurde erfolgreich aktualisiert")
                appendNewline(2)

                variableKey("Alte Beschreibung: ")
                appendNewline()
                variableValue(oldDescription.ifBlank { "Keine Beschreibung gesetzt" })
                appendNewline(2)

                variableKey("Neue Beschreibung: ")
                appendNewline()
                variableValue(poi.description.ifBlank { "Keine Beschreibung gesetzt" })
            }
        }

        type {
            notice(backButton(entry, poi))
        }
    }
}

private fun backButton(entry: TourEntry, poi: Poi): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zum Poi") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi))
        }
    }
}