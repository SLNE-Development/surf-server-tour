@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun renamePoiDialog(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("Name ändern") })
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        input {
            text("name") {
                label { text("Name") }
                maxLength(64)
                initial(poi.name)
                width(600)
            }
        }
    }

    type {
        confirmation(
            confirmRenameButton(entry, poi, editable),
            cancelRenameButton(entry, poi, editable)
        )
    }
}

private fun confirmRenameButton(
    entry: TourEntry,
    poi: Poi,
    editable: Boolean
) = actionButton {
    label { success("Speichern") }
    tooltip { info("Speichern und zurück zum POI") }

    action {
        customClick { response, audience ->
            val newName = response.getText("name") ?: "Fehler"

            plugin.launch {
                val oldName = poi.name

                EntryManager.updatePoi(entry, poi) {
                    it.name = newName
                }

                audience.showDialog(confirmNotice(entry, poi, oldName, editable))
            }
        }
    }
}

private fun confirmNotice(
    entry: TourEntry,
    poi: Poi,
    oldName: String,
    editable: Boolean
) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POIs") },
                poi.asComponent(),
                buildText { spacer("POI umbenannt") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Der POI wurde erfolgreich umbenannt")
                appendNewline(2)

                variableKey("Alter Name: ")
                appendNewline()
                variableValue(oldName)
                appendNewline(2)

                variableKey("Neuer Name: ")
                appendNewline()
                variableValue(poi.name)
            }
        }
    }

    type {
        notice(backToPoiButton(entry, poi, editable))
    }
}

private fun backToPoiButton(entry: TourEntry, poi: Poi, editable: Boolean) = actionButton {
    label { text("Zurück zum POI") }
    tooltip { info("Zurück zum POI") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi, editable))
        }
    }
}

private fun cancelRenameButton(entry: TourEntry, poi: Poi, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zum POI") }

    action {
        playerCallback {
            it.showDialog(onePoiDialog(entry, poi, editable))
        }
    }
}