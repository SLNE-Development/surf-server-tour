@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.dialogs.ownTourDialog
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.registry.data.dialog.DialogBase

fun descriptionOwnTourEntryDialog(entry: TourEntry) = dialog {
    base {
        title { primary("${entry.name} Beschreibung ändern") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        input {
            text("description") {
                label { text("Beschreibung") }
                initial(entry.description)
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 300)
            }
        }
    }

    type {
        confirmation(confirmDescriptionButton(entry), cancelDescriptionButton(entry))
    }
}

private fun confirmDescriptionButton(entry: TourEntry) = actionButton {
    label { success("Speichern") }
    tooltip { info("Speichern und zurück zur Einreichung") }

    action {
        customClick { response, audience ->
            val newDescription = response.getText("description") ?: "Fehler"

            plugin.launch {
                val oldDescription = entry.description

                EntryManager.updateEntry(entry) {
                    it.description = newDescription
                }

                audience.showDialog(confirmNotice(entry, oldDescription))
            }
        }
    }
}

private fun confirmNotice(entry: TourEntry, oldDescription: String) = dialog {
    base {
        title { primary("Beschreibung der Einreichung aktualisert") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                success("Die Beschreibung der Einreichung wurde erfolgreich aktualisiert")
            }
            plainMessage(400) {
                variableKey("Alte Beschreibung: ")
                variableValue(oldDescription)
            }
            plainMessage(400) {
                variableKey("Neue Beschreibung: ")
                variableValue(entry.description)
            }
        }
    }

    type {
        notice(backToEntryButton(entry))
    }
}

private fun backToEntryButton(entry: TourEntry) = actionButton {
    label { text("Zurück zur Einreichung") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry))
        }
    }
}

private fun cancelDescriptionButton(entry: TourEntry) = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zur Einreichung") }

    action {
        playerCallback {
            it.showDialog(ownTourDialog(entry))
        }
    }
}