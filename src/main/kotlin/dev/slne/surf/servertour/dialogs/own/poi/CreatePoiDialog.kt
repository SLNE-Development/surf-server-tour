@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.poi

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.dialogs.own.ownTourDialog
import dev.slne.surf.servertour.entry.*
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase
import java.util.*

fun createPoiDialog(entry: TourEntry, editable: Boolean) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("POI erstellen") })
        )
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        input {
            text("name") {
                label { text("Name") }
                maxLength(64)
                width(600)
            }

            text("description") {
                label { text("Beschreibung") }
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 300)
            }
        }
    }

    type {
        confirmation(confirmCreateButton(entry, editable), cancelCreateButton(entry, editable))
    }
}

private fun cancelCreateButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourDialog(entry, editable))
        }
    }
}

private fun confirmCreateButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { success("Erstellen") }
    tooltip { info("Erstellt den POI") }

    action {
        customPlayerClick { response, player ->
            val name = response.getText("name") ?: "Fehler"
            val description = response.getText("description") ?: "Fehler"

            plugin.launch {
                val poi = Poi(
                    uuid = UUID.randomUUID(),
                    name = name,
                    description = description,
                    owner = EntryMember(
                        uuid = player.uniqueId
                    ),
                    location = player.location,
                    status = EntryStatus.DRAFT,
                    entry = entry
                )

                EntryManager.addPoi(entry, poi)

                player.showDialog(createSuccessNotice(entry, poi, editable))
            }
        }
    }
}

private fun createSuccessNotice(entry: TourEntry, poi: Poi, editable: Boolean) = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                poi.asComponent(),
                buildText { spacer("POI erstellt") })
        )
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Der POI wurde erfolgreich erstellt!")
                appendNewline(2)

                variableKey("Name: ")
                appendNewline()
                variableValue(poi.name)
                appendNewline(2)

                variableKey("Beschreibung: ")
                appendNewline()
                variableValue(poi.description.ifBlank { "Keine Beschreibung vorhanden" })
            }
        }
    }

    type {
        notice {
            label { text("Zum POI") }
            tooltip { info("Zurück zum POI") }

            action {
                playerCallback { it.showDialog(onePoiDialog(entry, poi, editable)) }
            }
        }
    }
}